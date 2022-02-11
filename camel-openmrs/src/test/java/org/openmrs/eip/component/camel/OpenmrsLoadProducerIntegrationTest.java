package org.openmrs.eip.component.camel;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_CLASS;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_UUID;
import static org.openmrs.eip.component.Constants.QUERY_GET_HASH;
import static org.openmrs.eip.component.Constants.VALUE_SITE_SEPARATOR;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.BaseDbDrivenTest;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.Provider;
import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.management.hash.entity.ProviderHash;
import org.openmrs.eip.component.management.hash.entity.UserHash;
import org.openmrs.eip.component.model.ProviderModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:test_data.sql")
public class OpenmrsLoadProducerIntegrationTest extends BaseDbDrivenTest {
	
	private static final String PROVIDER_UUID = "2b3b12d1-5c4f-415f-871b-b98a22137606";
	
	private static final String USER_UUID = "1a3b12d1-5c4f-415f-871b-b98a22137605";
	
	private String creator = UserLight.class.getName() + "(" + USER_UUID + ")";
	
	private Exchange exchange;
	
	private OpenmrsLoadProducer producer;
	
	@Autowired
	private AbstractEntityService<User, UserModel> userService;
	
	@Autowired
	private AbstractEntityService<Provider, ProviderModel> providerService;
	
	@Before
	public void init() {
		exchange = new DefaultExchange(new DefaultCamelContext());
		producer = new OpenmrsLoadProducer(null, applicationContext, null);
	}
	
	@AfterClass
	public static void afterClass() {
		SyncContext.setUser(null);
	}
	
	@Test
	public void process_shouldPreProcessUserToUpdateUsernameAndSystemIdPropertiesToIncludeTheSendingSiteIdIfTheyDoNotExistInReceiverDatabase() {
		final String userUuid = "user-uuid";
		UserModel existingUser = userService.getModel(userUuid);
		assertNull(existingUser);
		final String username = "jdoe@eip.org";
		final String systemId = "123";
		final String siteId = "some-site-uuid";
		UserModel model = new UserModel();
		model.setUuid(userUuid);
		model.setUsername(username);
		model.setSystemId(systemId);
		model.setCreatorUuid(creator);
		model.setDateCreated(LocalDateTime.now());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		assertNull(userService.getModel(userUuid));
		
		producer.process(exchange);
		
		UserModel savedUser = userService.getModel(userUuid);
		assertNotNull(savedUser);
		assertEquals(username + VALUE_SITE_SEPARATOR + siteId, savedUser.getUsername());
		assertEquals(systemId + VALUE_SITE_SEPARATOR + siteId, savedUser.getSystemId());
	}
	
	@Test
	public void process_shouldPreProcessProviderToUpdateIdentifierPropertyToIncludeTheSendingSiteIdIfTheyDoNotExistInReceiverDatabase() {
		final String providerUuid = "provider-uuid";
		final String identifier = "12345";
		final String siteId = "some-site-uuid";
		ProviderModel model = new ProviderModel();
		model.setUuid(providerUuid);
		model.setIdentifier(identifier);
		model.setCreatorUuid(creator);
		model.setDateCreated(LocalDateTime.now());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		assertNull(providerService.getModel(providerUuid));
		
		producer.process(exchange);
		
		ProviderModel savedProvider = providerService.getModel(providerUuid);
		assertNotNull(savedProvider);
		assertEquals(identifier + VALUE_SITE_SEPARATOR + siteId, savedProvider.getIdentifier());
	}
	
	@Test
	public void process_shouldNotPreProcessProviderToUpdateIdentifierPropertyIfNotSet() {
		final String providerUuid = "provider-uuid";
		final String siteId = "some-site-uuid";
		ProviderModel model = new ProviderModel();
		model.setUuid(providerUuid);
		model.setCreatorUuid(creator);
		model.setDateCreated(LocalDateTime.now());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		assertNull(providerService.getModel(providerUuid));
		
		producer.process(exchange);
		
		ProviderModel savedProvider = providerService.getModel(providerUuid);
		assertNotNull(savedProvider);
		assertNull(savedProvider.getIdentifier());
	}
	
	@Test
	public void process_shouldPreProcessDeletedAUserAndMarkThemAsRetired() {
		UserModel existingUser = userService.getModel(USER_UUID);
		assertNotNull(existingUser);
		assertFalse(existingUser.isRetired());
		assertNull(existingUser.getRetiredByUuid());
		assertNull(existingUser.getRetireReason());
		assertNull(existingUser.getDateRetired());
		final String siteId = "some-site-uuid";
		UserModel model = new UserModel();
		model.setUuid(USER_UUID);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		metadata.setOperation("d");
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		UserLight user = new UserLight();
		final String appUserUuid = "test-user-uuid";
		user.setUuid(appUserUuid);
		SyncContext.setUser(user);
		UserHash existingHash = new UserHash();
		existingHash.setHash(HashUtils.computeHash(existingUser));
		final String query = QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, UserHash.class.getSimpleName())
		        .replace(PLACEHOLDER_UUID, USER_UUID);
		Mockito.when(producerTemplate.requestBody(query, null, List.class)).thenReturn(singletonList(existingHash));
		
		producer.process(exchange);
		
		existingUser = userService.getModel(USER_UUID);
		assertNotNull(existingUser);
		assertTrue(existingUser.isRetired());
		assertEquals(UserLight.class.getName() + "(" + appUserUuid + ")", existingUser.getRetiredByUuid());
		assertEquals(Constants.DEFAULT_RETIRE_REASON, existingUser.getRetireReason());
		assertNotNull(existingUser.getDateRetired());
	}
	
	@Test
	public void process_shouldPreProcessDeletedAProviderAndMarkThemAsRetired() {
		ProviderModel existingProvider = providerService.getModel(PROVIDER_UUID);
		assertNotNull(existingProvider);
		assertFalse(existingProvider.isRetired());
		assertNull(existingProvider.getRetiredByUuid());
		assertNull(existingProvider.getRetireReason());
		assertNull(existingProvider.getDateRetired());
		final String siteId = "some-site-uuid";
		ProviderModel model = new ProviderModel();
		model.setUuid(PROVIDER_UUID);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		metadata.setOperation("d");
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		UserLight user = new UserLight();
		final String appUserUuid = "test-user";
		user.setUuid(appUserUuid);
		SyncContext.setUser(user);
		ProviderHash existingHash = new ProviderHash();
		existingHash.setHash(HashUtils.computeHash(existingProvider));
		final String query = QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, ProviderHash.class.getSimpleName())
		        .replace(PLACEHOLDER_UUID, PROVIDER_UUID);
		Mockito.when(producerTemplate.requestBody(query, null, List.class)).thenReturn(singletonList(existingHash));
		
		producer.process(exchange);
		
		existingProvider = providerService.getModel(PROVIDER_UUID);
		assertNotNull(existingProvider);
		assertTrue(existingProvider.isRetired());
		assertEquals(UserLight.class.getName() + "(" + appUserUuid + ")", existingProvider.getRetiredByUuid());
		assertEquals(Constants.DEFAULT_RETIRE_REASON, existingProvider.getRetireReason());
		assertNotNull(existingProvider.getDateRetired());
	}
	
	@Test
	public void process_shouldNotCreateADeletedUserThatDoesNotExistExistInTheReceiver() {
		final String userUuid = "some-fake-user-uuid";
		assertNull(userService.getModel(userUuid));
		UserModel model = new UserModel();
		model.setUuid(userUuid);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		
		producer.process(exchange);
		
		assertNull(userService.getModel(userUuid));
	}
	
	@Test
	public void process_shouldNotCreateADeletedProviderThatDoesNotExistExistInTheReceiver() {
		final String providerUuid = "some-fake-provider-uuid";
		assertNull(providerService.getModel(providerUuid));
		ProviderModel model = new ProviderModel();
		model.setUuid(providerUuid);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		
		producer.process(exchange);
		
		assertNull(providerService.getModel(providerUuid));
	}
	
	@Test
	public void process_shouldNotPreProcessUserToIncludeTheSendingSiteIdIfTheyExistInReceiverDatabase() {
		UserModel existingUser = userService.getModel(USER_UUID);
		assertNotNull(existingUser);
		final String username = "jdoe@eip.org";
		final String systemId = "123";
		final String siteId = "some-site-uuid";
		UserModel model = new UserModel();
		model.setUuid(USER_UUID);
		model.setUsername(username);
		model.setSystemId(systemId);
		model.setCreatorUuid(creator);
		model.setDateCreated(LocalDateTime.now());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		final String query = Constants.QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, UserHash.class.getSimpleName())
		        .replace(PLACEHOLDER_UUID, USER_UUID);
		UserHash hash = new UserHash();
		hash.setHash(HashUtils.computeHash(existingUser));
		Mockito.when(producerTemplate.requestBody(query, null, List.class)).thenReturn(singletonList(hash));
		
		producer.process(exchange);
		
		UserModel savedUser = userService.getModel(USER_UUID);
		assertEquals(username, savedUser.getUsername());
		assertEquals(systemId, savedUser.getSystemId());
	}
	
	@Test
	public void process_shouldNotPreProcessProviderToIncludeTheSendingSiteIdIfTheyExistInReceiverDatabase() {
		ProviderModel existingProvider = providerService.getModel(PROVIDER_UUID);
		assertNotNull(existingProvider);
		final String identifier = "12345";
		final String siteId = "some-site-uuid";
		ProviderModel model = new ProviderModel();
		model.setUuid(PROVIDER_UUID);
		model.setIdentifier(identifier);
		model.setCreatorUuid(creator);
		model.setDateCreated(LocalDateTime.now());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		SyncModel syncModel = new SyncModel(model.getClass(), model, metadata);
		exchange.getIn().setBody(syncModel);
		final String query = Constants.QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, ProviderHash.class.getSimpleName())
		        .replace(PLACEHOLDER_UUID, PROVIDER_UUID);
		ProviderHash hash = new ProviderHash();
		hash.setHash(HashUtils.computeHash(existingProvider));
		Mockito.when(producerTemplate.requestBody(query, null, List.class)).thenReturn(singletonList(hash));
		
		producer.process(exchange);
		
		assertEquals(identifier, providerService.getModel(PROVIDER_UUID).getIdentifier());
	}
	
}
