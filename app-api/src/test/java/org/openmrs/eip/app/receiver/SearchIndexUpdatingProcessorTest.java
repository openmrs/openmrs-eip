package org.openmrs.eip.app.receiver;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.CACHE_EVICT;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.SEARCH_INDEX_UPDATE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_UPDATE_SEARCH_INDEX;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;

public class SearchIndexUpdatingProcessorTest {
	
	private SearchIndexUpdatingProcessor processor;
	
	private ParseContext jsonPathContext = JsonPath
	        .using(Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build());
	
	@Before
	public void setup() {
		processor = new SearchIndexUpdatingProcessor(null, null);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("search index update", processor.getProcessorName());
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
		PostSyncAction action = new PostSyncAction(msg, null);
		action.setId(id);
		assertEquals(
		    siteUuid + "-" + messageUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid + "-" + id,
		    processor.getThreadName(action));
	}
	
	@Test
	public void getUniqueId_shouldReturnEntityIdentifier() {
		final String uuid = "uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		assertEquals(uuid, processor.getUniqueId(new PostSyncAction(msg, null)));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassNameOfTheAssociatedSyncedMessage() {
		final String type = PersonModel.class.getName();
		SyncedMessage msg = new SyncedMessage();
		msg.setModelClassName(type);
		assertEquals(type, processor.getLogicalType(new PostSyncAction(msg, null)));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertEquals(2, processor.getLogicalTypeHierarchy(PersonModel.class.getName()).size());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("search-index-update", processor.getQueueName());
	}
	
	@Test
	public void processItem_shouldNotUpdateSearchIndexBeforeTheCacheIsCleared() {
		SyncedMessage msg = new SyncedMessage();
		PostSyncAction evict = new PostSyncAction(msg, CACHE_EVICT);
		PostSyncAction update = new PostSyncAction(msg, SEARCH_INDEX_UPDATE);
		msg.addAction(evict);
		msg.addAction(update);
		Assert.assertTrue(msg.requiresCacheEviction());
		processor = Mockito.spy(processor);
		
		processor.processItem(update);
		
		Mockito.verify(processor, Mockito.never()).send(any(), any(), any());
	}
	
	@Test
	public void processItem_shouldUpdateSearchIndexIfTheCacheIsCleared() {
		SyncedMessage msg = new SyncedMessage();
		PostSyncAction evict = new PostSyncAction(msg, CACHE_EVICT);
		evict.markAsCompleted();
		PostSyncAction update = new PostSyncAction(msg, SEARCH_INDEX_UPDATE);
		msg.addAction(evict);
		msg.addAction(update);
		Assert.assertFalse(msg.requiresCacheEviction());
		ProducerTemplate mockTemplate = Mockito.mock(ProducerTemplate.class);
		processor = Mockito.spy(new SearchIndexUpdatingProcessor(mockTemplate, null));
		Mockito.doNothing().when(processor).send(any(), any(), any());
		
		processor.processItem(update);
		
		Mockito.verify(processor).send(URI_UPDATE_SEARCH_INDEX, update, mockTemplate);
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForAPersonEntity() {
		final String personUuid = "person-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(personUuid);
		msg.setModelClassName(PersonModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		processor = Mockito.spy(processor);
		Mockito.doReturn(Arrays.asList(nameUuid1, nameUuid2)).when(processor).getPersonNameUuids(personUuid);
		Mockito.doReturn(Arrays.asList(idUuid1, idUuid2)).when(processor).getPatientIdentifierUuids(personUuid);
		
		List<String> payloads = (List) processor.convertBody(action);
		
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
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForADeletedPerson() {
		final String personUuid = "person-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(personUuid);
		msg.setModelClassName(PersonModel.class.getName());
		msg.setOperation(SyncOperation.d);
		PostSyncAction action = new PostSyncAction(msg, null);
		processor = Mockito.spy(processor);
		Mockito.doReturn(Arrays.asList(nameUuid1, nameUuid2)).when(processor).getPersonNameUuids(personUuid);
		Mockito.doReturn(Arrays.asList(idUuid1, idUuid2)).when(processor).getPatientIdentifierUuids(personUuid);
		
		List<String> payloads = (List) processor.convertBody(action);
		
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
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForAPatient() {
		final String patientUuid = "patient-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(patientUuid);
		msg.setModelClassName(PersonModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		processor = Mockito.spy(processor);
		Mockito.doReturn(Arrays.asList(nameUuid1, nameUuid2)).when(processor).getPersonNameUuids(patientUuid);
		Mockito.doReturn(Arrays.asList(idUuid1, idUuid2)).when(processor).getPatientIdentifierUuids(patientUuid);
		
		List<String> payloads = (List) processor.convertBody(action);
		
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
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForADeletedPatient() {
		final String patientUuid = "patient-uuid";
		final String nameUuid1 = "name-uuid-1";
		final String nameUuid2 = "name-uuid-2";
		final String idUuid1 = "id-uuid-1";
		final String idUuid2 = "id-uuid-2";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(patientUuid);
		msg.setModelClassName(PersonModel.class.getName());
		msg.setOperation(SyncOperation.d);
		PostSyncAction action = new PostSyncAction(msg, null);
		processor = Mockito.spy(processor);
		Mockito.doReturn(Arrays.asList(nameUuid1, nameUuid2)).when(processor).getPersonNameUuids(patientUuid);
		Mockito.doReturn(Arrays.asList(idUuid1, idUuid2)).when(processor).getPatientIdentifierUuids(patientUuid);
		
		List<String> payloads = (List) processor.convertBody(action);
		
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
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForAPersonName() {
		final String uuid = "name-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonNameModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForADeletedPersonName() {
		final String uuid = "name-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonNameModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("name", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForAPersonAttribute() {
		final String uuid = "attribute-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonAttributeModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForADeletedPersonAttribute() {
		final String uuid = "attribute-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PersonAttributeModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("person", JsonPath.read(json, "resource"));
		assertEquals("attribute", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForAPatientIdentifier() {
		final String uuid = "id-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PatientIdentifierModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		assertEquals("patient", JsonPath.read(json, "resource"));
		assertEquals("identifier", JsonPath.read(json, "subResource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
	}
	
	@Test
	public void convertBody_shouldGenerateSearchIndexUpdateJsonForADeletedPatientIdentifier() {
		final String uuid = "id-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setOperation(SyncOperation.d);
		msg.setModelClassName(PatientIdentifierModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("patient", JsonPath.read(json, "resource"));
		assertEquals("identifier", JsonPath.read(json, "subResource"));
		assertNull(docContext.read("uuid"));
	}
	
}
