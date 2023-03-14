package org.openmrs.eip.app.receiver;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome.CONFLICT;
import static org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome.ERROR;
import static org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome.SUCCESS;
import static org.openmrs.eip.app.receiver.ReceiverUtils.ID_URI;
import static org.openmrs.eip.app.receiver.ReceiverUtils.NAME_URI;
import static org.openmrs.eip.app.receiver.ReceiverUtils.generateEvictionPayload;
import static org.openmrs.eip.app.receiver.ReceiverUtils.generateSearchIndexUpdatePayload;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_UUID;
import static org.openmrs.eip.component.SyncOperation.c;
import static org.openmrs.eip.component.SyncOperation.d;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.BeanUtils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, CamelUtils.class })
public class ReceiverUtilsTest {
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	private ParseContext jsonPathContext = JsonPath
	        .using(Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build());
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(CamelUtils.class);
		when(SyncContext.getBean(ProducerTemplate.class)).thenReturn(mockTemplate);
		when(mockTemplate.getCamelContext()).thenReturn(new DefaultCamelContext());
	}
	
	@Test
	public void isCached_shouldReturnTrueForCachedEntities() {
		assertTrue(ReceiverUtils.isCached(PersonModel.class.getName()));
		assertTrue(ReceiverUtils.isCached(PersonNameModel.class.getName()));
		assertTrue(ReceiverUtils.isCached(PersonAddressModel.class.getName()));
		assertTrue(ReceiverUtils.isCached(PersonAttributeModel.class.getName()));
		assertTrue(ReceiverUtils.isCached(UserModel.class.getName()));
		assertTrue(ReceiverUtils.isCached(PatientModel.class.getName()));
	}
	
	@Test
	public void isCached_shouldReturnFalseForNonCachedEntities() {
		assertFalse(ReceiverUtils.isCached(PatientIdentifierModel.class.getName()));
	}
	
	@Test
	public void isIndexed_shouldReturnTrueForIndexedEntities() {
		assertTrue(ReceiverUtils.isIndexed(PersonNameModel.class.getName()));
		assertTrue(ReceiverUtils.isIndexed(PersonAttributeModel.class.getName()));
		assertTrue(ReceiverUtils.isIndexed(PatientIdentifierModel.class.getName()));
		assertTrue(ReceiverUtils.isIndexed(PersonModel.class.getName()));
		assertTrue(ReceiverUtils.isIndexed(PatientModel.class.getName()));
	}
	
	@Test
	public void isIndexed_shouldReturnFalseForNonIndexedEntities() {
		assertFalse(ReceiverUtils.isIndexed(PersonAddressModel.class.getName()));
	}
	
	@Test
	public void createSyncedMessage_shouldCreateASyncedMessageFromASyncMessageForACachedAndIndexedEntity() throws Exception {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(SyncMessage.class);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setId(1L);
		syncMessage.setDateCreated(new Date());
		syncMessage.setIdentifier("uuid");
		syncMessage.setEntityPayload("payload");
		syncMessage.setModelClassName(PersonModel.class.getName());
		syncMessage.setSite(new SiteInfo());
		syncMessage.setSnapshot(true);
		syncMessage.setMessageUuid("message-uuid");
		syncMessage.setDateSentBySender(LocalDateTime.now());
		syncMessage.setOperation(c);
		long timestamp = System.currentTimeMillis();
		
		SyncedMessage msg = ReceiverUtils.createSyncedMessage(syncMessage, SUCCESS);
		
		assertNull(msg.getId());
		assertTrue(msg.getDateCreated().getTime() == timestamp || msg.getDateCreated().getTime() > timestamp);
		assertEquals(syncMessage.getDateCreated(), msg.getDateReceived());
		assertFalse(msg.isResponseSent());
		assertTrue(msg.isCached());
		assertFalse(msg.isEvictedFromCache());
		assertTrue(msg.isIndexed());
		assertFalse(msg.isSearchIndexUpdated());
		assertEquals(SUCCESS, msg.getOutcome());
		Set<String> ignored = new HashSet();
		ignored.add("id");
		ignored.add("class");
		ignored.add("dateCreated");
		for (PropertyDescriptor descriptor : descriptors) {
			if (ignored.contains(descriptor.getName())) {
				continue;
			}
			
			String getter = descriptor.getReadMethod().getName();
			assertEquals(invokeMethod(syncMessage, getter), invokeMethod(msg, getter));
		}
	}
	
	@Test
	public void createSyncedMessage_shouldCreateASyncedMessageForACachedButNotIndexedEntity() {
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setModelClassName(PersonAddressModel.class.getName());
		
		SyncedMessage msg = ReceiverUtils.createSyncedMessage(syncMessage, SUCCESS);
		
		assertEquals(SUCCESS, msg.getOutcome());
		assertTrue(msg.isCached());
		assertFalse(msg.isEvictedFromCache());
		assertFalse(msg.isIndexed());
		assertFalse(msg.isSearchIndexUpdated());
	}
	
	@Test
	public void createSyncedMessage_shouldCreateASyncedMessageForAnIndexedButNotCachedEntity() {
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setModelClassName(PatientIdentifierModel.class.getName());
		
		SyncedMessage msg = ReceiverUtils.createSyncedMessage(syncMessage, SUCCESS);
		
		assertEquals(SUCCESS, msg.getOutcome());
		assertTrue(msg.isIndexed());
		assertFalse(msg.isSearchIndexUpdated());
		assertFalse(msg.isCached());
		assertFalse(msg.isEvictedFromCache());
	}
	
	@Test
	public void createSyncedMessage_shouldCreateASyncedMessageForAFailedItem() {
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setModelClassName(PersonModel.class.getName());
		
		SyncedMessage msg = ReceiverUtils.createSyncedMessage(syncMessage, ERROR);
		
		assertEquals(ERROR, msg.getOutcome());
		assertTrue(msg.isIndexed());
		assertFalse(msg.isSearchIndexUpdated());
		assertTrue(msg.isCached());
		assertFalse(msg.isEvictedFromCache());
	}
	
	@Test
	public void createSyncedMessage_shouldCreateASyncedMessageFromForAConflictedItem() {
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setModelClassName(PersonModel.class.getName());
		
		SyncedMessage msg = ReceiverUtils.createSyncedMessage(syncMessage, CONFLICT);
		
		assertEquals(CONFLICT, msg.getOutcome());
		assertTrue(msg.isIndexed());
		assertFalse(msg.isSearchIndexUpdated());
		assertTrue(msg.isCached());
		assertFalse(msg.isEvictedFromCache());
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAPersonEntity() {
		final String uuid = "person-uuid";
		
		String json = generateEvictionPayload(PersonModel.class.getName(), uuid, c).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedPerson() {
		final String uuid = "person-uuid";
		
		String json = generateEvictionPayload(PersonModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertNull(docContext.read("uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAPatient() {
		final String uuid = "patient-uuid";
		
		String json = generateEvictionPayload(PatientModel.class.getName(), uuid, c).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedPatient() {
		final String uuid = "patient-uuid";
		
		String json = generateEvictionPayload(PatientModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertNull(docContext.read("uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAPersonName() {
		final String uuid = "name-uuid";
		
		String json = generateEvictionPayload(PersonNameModel.class.getName(), uuid, c).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedPersonName() {
		final String uuid = "name-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonNameModel.class.getName());
		
		String json = generateEvictionPayload(PersonNameModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAPersonAttribute() {
		final String uuid = "attribute-uuid";
		
		String json = generateEvictionPayload(PersonAttributeModel.class.getName(), uuid, c).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedPersonAttribute() {
		final String uuid = "attribute-uuid";
		
		String json = generateEvictionPayload(PersonAttributeModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAPersonAddress() {
		final String uuid = "address-uuid";
		
		String json = generateEvictionPayload(PersonAddressModel.class.getName(), uuid, c).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("address", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedPersonAddress() {
		final String uuid = "address-uuid";
		
		String json = generateEvictionPayload(PersonAddressModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("address", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForAUser() {
		final String uuid = "user-uuid";
		
		String json = generateEvictionPayload(UserModel.class.getName(), uuid, c).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("user", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateEvictionPayload_shouldGenerateCacheEvictionJsonForADeletedUser() {
		final String uuid = "user-uuid";
		
		String json = generateEvictionPayload(UserModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("user", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForAPersonEntity() {
		final String personUuid = "person-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		String nameUri = NAME_URI.replace(PLACEHOLDER_UUID, personUuid);
		String idUri = ID_URI.replace(PLACEHOLDER_UUID, personUuid);
		when(CamelUtils.send(eq(nameUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", nameUuid1), singletonMap("uuid", nameUuid2)));
			return exchange;
		});
		when(CamelUtils.send(eq(idUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", idUuid1), singletonMap("uuid", idUuid2)));
			return exchange;
		});
		
		List<String> payloads = (List) generateSearchIndexUpdatePayload(PersonModel.class.getName(), personUuid, c);
		
		assertEquals(4, payloads.size());
		assertEquals("person", JsonPath.read(payloads.get(0), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(0), "subResource"));
		assertEquals(nameUuid1, JsonPath.read(payloads.get(0), "uuid"));
		assertEquals("person", JsonPath.read(payloads.get(1), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(1), "subResource"));
		assertEquals(nameUuid2, JsonPath.read(payloads.get(1), "uuid"));
		
		assertEquals("patient", JsonPath.read(payloads.get(2), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(2), "subResource"));
		assertEquals(idUuid1, JsonPath.read(payloads.get(2), "uuid"));
		assertEquals("patient", JsonPath.read(payloads.get(3), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(3), "subResource"));
		assertEquals(idUuid2, JsonPath.read(payloads.get(3), "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForADeletedPerson() {
		final String personUuid = "person-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		String nameUri = NAME_URI.replace(PLACEHOLDER_UUID, personUuid);
		String idUri = ID_URI.replace(PLACEHOLDER_UUID, personUuid);
		when(CamelUtils.send(eq(nameUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", nameUuid1), singletonMap("uuid", nameUuid2)));
			return exchange;
		});
		when(CamelUtils.send(eq(idUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", idUuid1), singletonMap("uuid", idUuid2)));
			return exchange;
		});
		
		List<String> payloads = (List) generateSearchIndexUpdatePayload(PersonModel.class.getName(), personUuid, d);
		
		assertEquals(4, payloads.size());
		assertEquals("person", JsonPath.read(payloads.get(0), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(0), "subResource"));
		assertEquals(nameUuid1, JsonPath.read(payloads.get(0), "uuid"));
		assertEquals("person", JsonPath.read(payloads.get(1), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(1), "subResource"));
		assertEquals(nameUuid2, JsonPath.read(payloads.get(1), "uuid"));
		
		assertEquals("patient", JsonPath.read(payloads.get(2), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(2), "subResource"));
		assertEquals(idUuid1, JsonPath.read(payloads.get(2), "uuid"));
		assertEquals("patient", JsonPath.read(payloads.get(3), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(3), "subResource"));
		assertEquals(idUuid2, JsonPath.read(payloads.get(3), "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForAPatient() {
		final String patientUuid = "patient-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		String nameUri = NAME_URI.replace(PLACEHOLDER_UUID, patientUuid);
		String idUri = ID_URI.replace(PLACEHOLDER_UUID, patientUuid);
		when(CamelUtils.send(eq(nameUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", nameUuid1), singletonMap("uuid", nameUuid2)));
			return exchange;
		});
		when(CamelUtils.send(eq(idUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", idUuid1), singletonMap("uuid", idUuid2)));
			return exchange;
		});
		
		List<String> payloads = (List) generateSearchIndexUpdatePayload(PatientModel.class.getName(), patientUuid, c);
		
		assertEquals(4, payloads.size());
		assertEquals("person", JsonPath.read(payloads.get(0), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(0), "subResource"));
		assertEquals(nameUuid1, JsonPath.read(payloads.get(0), "uuid"));
		assertEquals("person", JsonPath.read(payloads.get(1), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(1), "subResource"));
		assertEquals(nameUuid2, JsonPath.read(payloads.get(1), "uuid"));
		
		assertEquals("patient", JsonPath.read(payloads.get(2), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(2), "subResource"));
		assertEquals(idUuid1, JsonPath.read(payloads.get(2), "uuid"));
		assertEquals("patient", JsonPath.read(payloads.get(3), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(3), "subResource"));
		assertEquals(idUuid2, JsonPath.read(payloads.get(3), "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForADeletedPatient() {
		final String patientUuid = "patient-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(patientUuid);
		msg.setModelClassName(PatientModel.class.getName());
		msg.setOperation(SyncOperation.d);
		String nameUri = NAME_URI.replace(PLACEHOLDER_UUID, patientUuid);
		String idUri = ID_URI.replace(PLACEHOLDER_UUID, patientUuid);
		when(CamelUtils.send(eq(nameUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", nameUuid1), singletonMap("uuid", nameUuid2)));
			return exchange;
		});
		when(CamelUtils.send(eq(idUri), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchange.getMessage().setBody(asList(singletonMap("uuid", idUuid1), singletonMap("uuid", idUuid2)));
			return exchange;
		});
		
		List<String> payloads = (List) generateSearchIndexUpdatePayload(PatientModel.class.getName(), patientUuid, d);
		
		assertEquals(4, payloads.size());
		assertEquals("person", JsonPath.read(payloads.get(0), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(0), "subResource"));
		assertEquals(nameUuid1, JsonPath.read(payloads.get(0), "uuid"));
		assertEquals("person", JsonPath.read(payloads.get(1), "resource"));
		assertEquals("name", JsonPath.read(payloads.get(1), "subResource"));
		assertEquals(nameUuid2, JsonPath.read(payloads.get(1), "uuid"));
		
		assertEquals("patient", JsonPath.read(payloads.get(2), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(2), "subResource"));
		assertEquals(idUuid1, JsonPath.read(payloads.get(2), "uuid"));
		assertEquals("patient", JsonPath.read(payloads.get(3), "resource"));
		assertEquals("identifier", JsonPath.read(payloads.get(3), "subResource"));
		assertEquals(idUuid2, JsonPath.read(payloads.get(3), "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForAPersonName() {
		final String uuid = "name-uuid";
		
		String json = generateSearchIndexUpdatePayload(PersonNameModel.class.getName(), uuid, c).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForADeletedPersonName() {
		final String uuid = "name-uuid";
		
		String json = generateSearchIndexUpdatePayload(PersonNameModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForAPersonAttribute() {
		final String uuid = "attribute-uuid";
		
		String json = generateSearchIndexUpdatePayload(PersonAttributeModel.class.getName(), uuid, c).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForADeletedPersonAttribute() {
		final String uuid = "attribute-uuid";
		
		String json = generateSearchIndexUpdatePayload(PersonAttributeModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForAPatientIdentifier() {
		final String uuid = "id-uuid";
		
		String json = generateSearchIndexUpdatePayload(PatientIdentifierModel.class.getName(), uuid, c).toString();
		
		assertEquals("patient", JsonPath.read(json, "resource"));
		assertEquals("identifier", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void generateSearchIndexUpdatePayload_shouldGenerateSearchIndexUpdateJsonForADeletedPatientIdentifier() {
		final String uuid = "id-uuid";
		
		String json = generateSearchIndexUpdatePayload(PatientIdentifierModel.class.getName(), uuid, d).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("patient", JsonPath.read(json, "resource"));
		assertEquals("identifier", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
}
