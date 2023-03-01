package org.openmrs.eip.app.receiver;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;

public class CacheEvictingProcessorTest {
	
	private CacheEvictingProcessor processor = new CacheEvictingProcessor(null, null);
	
	private ParseContext jsonPathContext = JsonPath
	        .using(Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build());
	
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
		assertEquals("cache-evict", processor.getQueueName());
	}
	
	@Test
	public void convertBody_shouldGenerateCacheEvictionJsonForAPersonEntity() {
		final String uuid = "person-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(PersonModel.class.getName());
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
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
		PostSyncAction action = new PostSyncAction(msg, null);
		
		String json = processor.convertBody(action).toString();
		
		DocumentContext docContext = jsonPathContext.parse(json);
		assertEquals("user", JsonPath.read(json, "resource"));
		assertEquals(uuid, JsonPath.read(json, "uuid"));
		assertNull(docContext.read("subResource"));
	}
	
}
