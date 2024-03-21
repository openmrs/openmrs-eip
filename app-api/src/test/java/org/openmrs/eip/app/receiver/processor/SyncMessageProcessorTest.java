package org.openmrs.eip.app.receiver.processor;

import static java.util.Collections.synchronizedSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtils.class)
public class SyncMessageProcessorTest {
	
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Mock
	private SyncHelper mockHelper;
	
	@Mock
	private ReceiverService mockService;
	
	@Mock
	private SyncModel mockSyncModel;
	
	private SyncMessageProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(JsonUtils.class);
		setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncMessageProcessor(EXECUTOR, mockService, mockHelper);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
		setInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE", (Object) null);
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final String uuid = "uuid";
		SyncMessage msg = new SyncMessage();
		msg.setIdentifier(uuid);
		assertEquals(uuid, processor.getUniqueId(msg));
	}
	
	@Test
	public void getThreadName_shouldReturnThreadName() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final String siteUuid = "site-uuid";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(PersonModel.class.getName());
		msg.setIdentifier(uuid);
		msg.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		msg.setSite(siteInfo);
		assertEquals(siteUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid + "-" + messageUuid,
		    processor.getThreadName(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassName() {
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		assertEquals(VisitModel.class.getName(), processor.getLogicalType(msg));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		final String clazz = PatientModel.class.getName();
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(clazz);
		assertEquals(Utils.getListOfModelClassHierarchy(clazz), processor.getLogicalTypeHierarchy(clazz));
	}
	
	@Test
	public void processItem_shouldSyncTheMessage() {
		final String uuid = "uuid";
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload(payload);
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Set<String> procMsgQueue = synchronizedSet(new HashSet<>());
		setInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE", procMsgQueue);
		
		processor.processItem(msg);
		
		Mockito.verify(mockHelper).sync(mockSyncModel, false);
		Mockito.verify(mockService).moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
		Assert.assertTrue(procMsgQueue.isEmpty());
	}
	
	@Test
	public void processItem_shouldAddTheItemToTheConflictQueueIfAConflictIsDetected() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Mockito.doThrow(new ConflictsFoundException()).when(mockHelper).sync(mockSyncModel, false);
		
		processor.processItem(msg);
		
		Mockito.verify(mockService).processConflictedSyncItem(msg);
	}
	
	@Test
	public void processItem_shouldFailIfTheEntityHasAnItemInTheErrorQueue() {
		final String uuid = "uuid";
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setIdentifier(uuid);
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		when(mockService.hasRetryItem(uuid, msg.getModelClassName())).thenReturn(true);
		
		processor.processItem(msg);
		
		Mockito.verify(mockService).processFailedSyncItem(msg, EIPException.class.getName(),
		    "Entity still has earlier items in the retry queue");
	}
	
	@Test
	public void processItem_shouldFailIfTheEntityHasAnItemInTheErrorQueueWithRootCause() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		final String rootCauseMsg = "test root error";
		Exception ex = new EIPException("test1", new Exception("test2", new ActiveMQException(rootCauseMsg)));
		Mockito.doThrow(ex).when(mockHelper).sync(mockSyncModel, false);
		
		processor.processItem(msg);
		
		Mockito.verify(mockService).processFailedSyncItem(msg, ActiveMQException.class.getName(), rootCauseMsg);
	}
	
	@Test
	public void processItem_shouldSkipASubclassMessageIfAnotherSiteThreadIsProcessingAnEventForTheSameEntity() {
		final String uuid = "visit-uuid";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setIdentifier(uuid);
		final String uniqueId = VisitModel.class.getName() + "#" + uuid;
		Set<String> procMsgQueue = synchronizedSet(new HashSet<>());
		Assert.assertTrue(procMsgQueue.add(uniqueId));
		setInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE", procMsgQueue);
		
		processor.processItem(msg);
		
		PowerMockito.verifyZeroInteractions(mockService);
		PowerMockito.verifyZeroInteractions(mockHelper);
	}
	
}
