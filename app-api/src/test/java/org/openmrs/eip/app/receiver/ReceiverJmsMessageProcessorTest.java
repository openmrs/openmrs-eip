package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType.SYNC;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CamelUtils.class)
public class ReceiverJmsMessageProcessorTest {
	
	private ReceiverJmsMessageProcessor processor;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Mock
	private ReceiverReconcileService mockReconcileService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CamelUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReceiverJmsMessageProcessor(null, mockReceiverService, mockReconcileService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getUniqueId_shouldReturnEntityIdentifierForASyncMessage() throws Exception {
		JmsMessage msg = new JmsMessage();
		msg.setType(SYNC);
		final String uuid = "person-uuid";
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		SyncModel syncModel = SyncModel.builder().model(model).tableToSyncModelClass(PersonModel.class).build();
		msg.setBody(JsonUtils.marshalToBytes(syncModel));
		assertEquals(uuid, processor.getUniqueId(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnSiteIdentifierForAReconciliationMessage() {
		final String siteIdentifier = "test";
		JmsMessage msg = new JmsMessage();
		msg.setSiteId(siteIdentifier);
		msg.setType(MessageType.RECONCILE);
		assertEquals(siteIdentifier, processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTypeForReconcileItem() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.RECONCILE);
		assertEquals(MessageType.RECONCILE.name(), processor.getLogicalType(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheTableToSyncModelClassNameForSyncItem() {
		JmsMessage msg = new JmsMessage();
		msg.setType(SYNC);
		SyncModel model = SyncModel.builder().tableToSyncModelClass(VisitModel.class).build();
		msg.setBody(JsonUtils.marshalToBytes(model));
		assertEquals(VisitModel.class.getName(), processor.getLogicalType(msg));
	}
	
	@Test
	public void processItem_shouldProcessASyncMessage() {
		JmsMessage msg = new JmsMessage();
		msg.setType(SYNC);
		
		processor.processItem(msg);
		
		Mockito.verify(mockReceiverService).processJmsMessage(msg);
	}
	
	@Test
	public void processItem_shouldProcessAReconcileMessage() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.RECONCILE);
		
		processor.processItem(msg);
		
		Mockito.verify(mockReconcileService).processJmsMessage(msg);
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchyForSyncItem() {
		final String clazz = PatientModel.class.getName();
		assertEquals(Utils.getListOfModelClassHierarchy(clazz), processor.getLogicalTypeHierarchy(clazz));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnNullForReconcileItem() {
		assertNull(processor.getLogicalTypeHierarchy(MessageType.RECONCILE.name()));
	}
	
}
