package org.openmrs.eip.app.receiver.processor;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ReceiverRetryProcessorTest {
	
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Mock
	private SyncHelper mockHelper;
	
	@Mock
	private ReceiverService mockService;
	
	private ReceiverRetryProcessor processor;
	
	@Before
	public void setup() {
		setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReceiverRetryProcessor(EXECUTOR, mockService, mockHelper);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final String uuid = "uuid";
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		assertEquals(uuid, processor.getUniqueId(retry));
	}
	
	@Test
	public void getThreadName_shouldReturnThreadName() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final String siteUuid = "site-uuid";
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(PersonModel.class.getName());
		retry.setIdentifier(uuid);
		retry.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		retry.setSite(siteInfo);
		assertEquals(siteUuid + "-" + AppUtils.getSimpleName(retry.getModelClassName()) + "-" + uuid + "-" + messageUuid,
		    processor.getThreadName(retry));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassName() {
		ReceiverRetryQueueItem msg = new ReceiverRetryQueueItem();
		msg.setModelClassName(VisitModel.class.getName());
		assertEquals(VisitModel.class.getName(), processor.getLogicalType(msg));
	}
	
	@Test
	public void getSyncPayload_shouldReturnPayload() {
		final String payload = "{}";
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setEntityPayload(payload);
		Assert.assertEquals(payload, processor.getSyncPayload(retry));
	}
	
}
