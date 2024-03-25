package org.openmrs.eip.app.receiver.processor;

import static java.util.Collections.synchronizedSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtils.class, AppUtils.class })
public class BaseSyncProcessorTest {
	
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Mock
	private SyncHelper mockHelper;
	
	@Mock
	private SyncModel mockSyncModel;
	
	private BaseSyncProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = Mockito.spy(new SyncMessageProcessor(EXECUTOR, null, mockHelper));
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
		setInternalState(BaseSyncProcessor.class, "PROCESSING_MSG_QUEUE", Collections.synchronizedSet(new HashSet<>()));
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
		Mockito.doNothing().when(processor).beforeSync(msg);
		Mockito.doNothing().when(processor).afterSync(msg);
		Set<String> procMsgQueue = synchronizedSet(new HashSet<>());
		setInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE", procMsgQueue);
		
		processor.processItem(msg);
		
		Mockito.verify(mockHelper).sync(mockSyncModel, false);
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor).afterSync(msg);
		Assert.assertTrue(procMsgQueue.isEmpty());
	}
	
	@Test
	public void processItem_shouldAddTheItemToTheConflictQueueIfAConflictIsDetected() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Mockito.doNothing().when(processor).beforeSync(msg);
		Mockito.doNothing().when(processor).onConflict(msg);
		Mockito.doThrow(new ConflictsFoundException()).when(mockHelper).sync(mockSyncModel, false);
		
		processor.processItem(msg);
		
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor).onConflict(msg);
		Mockito.verify(processor, never()).afterSync(msg);
	}
	
	@Test
	public void processItem_shouldFailIfTheAnErrorIsEncountered() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Exception ex = new EIPException("test");
		Mockito.doThrow(ex).when(mockHelper).sync(mockSyncModel, false);
		Mockito.doNothing().when(processor).beforeSync(msg);
		Mockito.doNothing().when(processor).onError(msg, ex.getClass().getName(), ex.getMessage());
		
		processor.processItem(msg);
		
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor).onError(msg, ex.getClass().getName(), ex.getMessage());
		Mockito.verify(processor, never()).afterSync(msg);
	}
	
	@Test
	public void processItem_shouldTruncateTheErrorMessageIfLongerThenTheThreshold() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		final String errorMsg = RandomStringUtils.randomAscii(1025);
		Exception ex = new EIPException(errorMsg);
		Mockito.doThrow(ex).when(mockHelper).sync(mockSyncModel, false);
		Mockito.doNothing().when(processor).beforeSync(msg);
		Mockito.doNothing().when(processor).onError(msg, ex.getClass().getName(), errorMsg.substring(0, 1024));
		
		processor.processItem(msg);
		
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor).onError(msg, ex.getClass().getName(), errorMsg.substring(0, 1024));
		Mockito.verify(processor, never()).afterSync(msg);
	}
	
	@Test
	public void processItem_shouldFailIfTheAnErrorIsEncounteredWithRootCause() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Exception rootEx = new ActiveMQException();
		Mockito.doThrow(new EIPException("test", new Exception(rootEx))).when(mockHelper).sync(mockSyncModel, false);
		Mockito.doNothing().when(processor).beforeSync(msg);
		Mockito.doNothing().when(processor).onError(msg, rootEx.getClass().getName(), rootEx.getMessage());
		
		processor.processItem(msg);
		
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor).onError(msg, rootEx.getClass().getName(), rootEx.getMessage());
		Mockito.verify(processor, never()).afterSync(msg);
	}
	
	@Test
	public void processItem_shouldSkipASubclassMessageIfAnotherThreadIsProcessingAnEventForTheSameEntity() {
		final String uuid = "visit-uuid";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setIdentifier(uuid);
		final String uniqueId = VisitModel.class.getName() + "#" + uuid;
		Set<String> procMsgQueue = synchronizedSet(new HashSet<>());
		Assert.assertTrue(procMsgQueue.add(uniqueId));
		setInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE", procMsgQueue);
		
		processor.processItem(msg);
		
		PowerMockito.verifyZeroInteractions(mockHelper);
		Mockito.verify(processor, never()).beforeSync(msg);
		Mockito.verify(processor, never()).getSyncPayload(msg);
		Mockito.verify(processor, never()).afterSync(msg);
		Mockito.verify(processor, never()).onConflict(msg);
		Mockito.verify(processor, never()).onError(eq(msg), any(), any());
	}
	
	@Test
	public void processItem_shouldNotCallOnErrorWhenAnErrorEncounteredAndApplicationIsShuttingDown() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload("{}");
		when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		Exception ex = new EIPException("test");
		Mockito.doThrow(ex).when(mockHelper).sync(mockSyncModel, false);
		Mockito.doNothing().when(processor).beforeSync(msg);
		when(AppUtils.isShuttingDown()).thenReturn(true);
		
		processor.processItem(msg);
		
		Mockito.verify(processor).beforeSync(msg);
		Mockito.verify(processor, never()).onError(msg, ex.getClass().getName(), ex.getMessage());
		Mockito.verify(processor, never()).afterSync(msg);
	}
	
}
