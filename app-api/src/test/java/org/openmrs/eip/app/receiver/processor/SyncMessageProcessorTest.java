package org.openmrs.eip.app.receiver.processor;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.SyncHelper;
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
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncMessageProcessor(EXECUTOR, mockService, mockHelper);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", false);
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
		final String payload = "{}";
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(VisitModel.class.getName());
		msg.setEntityPayload(payload);
		Mockito.when(JsonUtils.unmarshalSyncModel(payload)).thenReturn(mockSyncModel);
		
		processor.processItem(msg);
		
		Mockito.verify(mockHelper).sync(mockSyncModel, false);
	}
	
}
