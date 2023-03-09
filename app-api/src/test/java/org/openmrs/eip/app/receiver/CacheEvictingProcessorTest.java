package org.openmrs.eip.app.receiver;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.powermock.reflect.Whitebox;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;

public class CacheEvictingProcessorTest {
	
	private CacheEvictingProcessor processor;
	
	private ParseContext jsonPathContext = JsonPath
	        .using(Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build());
	
	@Before
	public void setup() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new CacheEvictingProcessor(null, null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("cache evict", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingTheAssociatedSyncedMessageDetails() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final Long id = 2L;
		final String siteUuid = "site-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setModelClassName(PersonModel.class.getName());
		msg.setIdentifier(uuid);
		msg.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		msg.setSite(siteInfo);
		assertEquals(siteUuid + "-" + messageUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid,
		    processor.getThreadName(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnEntityIdentifier() {
		final String uuid = "uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		assertEquals(uuid, processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassNameOfTheAssociatedSyncedMessage() {
		final String type = PersonModel.class.getName();
		SyncedMessage msg = new SyncedMessage();
		msg.setModelClassName(type);
		assertEquals(type, processor.getLogicalType(msg));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertEquals(2, processor.getLogicalTypeHierarchy(PersonModel.class.getName()).size());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("cache-evict", processor.getQueueName());
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPersonEntity() {
		final String uuid = "person-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedPerson() {
		final String uuid = "person-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertNull(docContext.read("uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPatient() {
		final String uuid = "patient-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PatientModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedPatient() {
		final String uuid = "patient-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PatientModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertNull(docContext.read("uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPersonName() {
		final String uuid = "name-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonNameModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedPersonName() {
		final String uuid = "name-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonNameModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPersonAttribute() {
		final String uuid = "attribute-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonAttributeModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedPersonAttribute() {
		final String uuid = "attribute-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonAttributeModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPersonAddress() {
		final String uuid = "address-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonAddressModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("address", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedPersonAddress() {
		final String uuid = "address-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonAddressModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("address", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAUser() {
		final String uuid = "user-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(UserModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("user", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForADeletedUser() {
		final String uuid = "user-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(UserModel.class.getName());
		
		String json = processor.convertBody(msg).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("user", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void onSuccess_shouldMarkTheMessageAsProcessed() {
		SyncedMessage msg = new SyncedMessage();
		assertNull(msg.getEvictedFromCache());
		SyncedMessageRepository mockRepo = Mockito.mock(SyncedMessageRepository.class);
		processor = new CacheEvictingProcessor(null, null, mockRepo);
		
		processor.onSuccess(msg);
		
		assertTrue(msg.getEvictedFromCache());
		Mockito.verify(mockRepo).save(msg);
	}
	
}
