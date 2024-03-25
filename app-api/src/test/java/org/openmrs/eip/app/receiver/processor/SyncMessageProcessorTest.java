package org.openmrs.eip.app.receiver.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SyncMessageProcessorTest {
	
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Mock
	private SyncHelper mockHelper;
	
	@Mock
	private ReceiverService mockService;
	
	private SyncMessageProcessor processor;
	
	@Before
	public void setup() {
		setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncMessageProcessor(EXECUTOR, mockService, mockHelper);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
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
	public void beforeSync_shouldFailIfTheEntityHasAnItemInTheErrorQueue() {
		final String uuid = "uuid";
		SyncMessage msg = new SyncMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(VisitModel.class.getName());
		when(mockService.hasRetryItem(uuid, msg.getModelClassName())).thenReturn(true);
		
		processor.processItem(msg);
		
		Mockito.verify(mockService).processFailedSyncItem(msg, EIPException.class.getName(),
		    "Entity still has earlier items in the retry queue");
	}
	
	@Test
	public void getSyncPayload_shouldReturnPayload() {
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setEntityPayload(payload);
		Assert.assertEquals(payload, processor.getSyncPayload(msg));
	}
	
	@Test
	public void afterSync_shouldMoveItemToSyncQueue() {
		SyncMessage msg = new SyncMessage();
		
		processor.afterSync(msg);
		
		Mockito.verify(mockService).moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
	}
	
	@Test
	public void onConflict_shouldAddTheItemToTheConflictQueueIfAConflictIsDetected() {
		SyncMessage msg = new SyncMessage();
		
		processor.onConflict(msg);
		
		Mockito.verify(mockService).processConflictedSyncItem(msg);
	}
	
	@Test
	public void onError_shouldAddItemToTheRetryQueue() {
		final String errorMsg = "test";
		SyncMessage msg = new SyncMessage();
		Exception ex = new EIPException(errorMsg);
		
		processor.onError(msg, ex.getClass().getName(), ex.getMessage());
		
		Mockito.verify(mockService).processFailedSyncItem(msg, EIPException.class.getName(), errorMsg);
	}
	
}
