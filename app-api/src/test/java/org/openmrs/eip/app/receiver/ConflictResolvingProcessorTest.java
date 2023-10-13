package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class ConflictResolvingProcessorTest {
	
	private ConflictResolvingProcessor processor;
	
	@Mock
	private ConflictService mockConflictService;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		Mockito.when(SyncContext.getBean(ConflictService.class)).thenReturn(mockConflictService);
		Mockito.when(SyncContext.getBean(ReceiverService.class)).thenReturn(mockReceiverService);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = ConflictResolvingProcessor.getInstance();
		setInternalState(processor, "conflictService", mockConflictService);
		setInternalState(processor, "receiverService", mockReceiverService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("conflict resolver", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final String siteUuid = "site-uuid";
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setModelClassName(PersonModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		conflict.setSite(siteInfo);
		assertEquals(siteUuid + "-" + AppUtils.getSimpleName(conflict.getModelClassName()) + "-" + uuid + "-" + messageUuid,
		    processor.getThreadName(conflict));
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final Long id = 7L;
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setId(id);
		assertEquals(id.toString(), processor.getUniqueId(conflict));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassName() {
		assertEquals(ConflictQueueItem.class.getName(), processor.getLogicalType(new ConflictQueueItem()));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertNull(processor.getLogicalTypeHierarchy(PersonModel.class.getName()));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("conflict-resolver", processor.getQueueName());
	}
	
	@Test
	public void processItem_shouldSkipConflictIfItHasNoItemsInTheSyncAndRetryQueue() throws Exception {
		final String uuid = "uuid";
		final String modelClass = PersonModel.class.getName();
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setIdentifier(uuid);
		conflict.setModelClassName(modelClass);
		
		processor.processItem(conflict);
		
		Mockito.verify(mockConflictService, Mockito.never()).resolve(ArgumentMatchers.any());
	}
	
	@Test
	public void processItem_shouldProcessTheConflictIfItHasAnItemInTheSyncQueue() throws Exception {
		final String uuid = "uuid";
		final String modelClass = PersonModel.class.getName();
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setIdentifier(uuid);
		conflict.setModelClassName(modelClass);
		Mockito.when(mockReceiverService.hasSyncItem(uuid, modelClass)).thenReturn(true);
		
		processor.processItem(conflict);
		
		ArgumentCaptor<ConflictResolution> argCaptor = ArgumentCaptor.forClass(ConflictResolution.class);
		Mockito.verify(mockConflictService).resolve(argCaptor.capture());
		Assert.assertEquals(ResolutionDecision.SYNC_NEW, argCaptor.getValue().getDecision());
		Assert.assertEquals(conflict, argCaptor.getValue().getConflict());
	}
	
	@Test
	public void processItem_shouldProcessTheConflictIfItHasAnItemInTheRetryQueue() throws Exception {
		final String uuid = "uuid";
		final String modelClass = PersonModel.class.getName();
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setIdentifier(uuid);
		conflict.setModelClassName(modelClass);
		Mockito.when(mockReceiverService.hasRetryItem(uuid, modelClass)).thenReturn(true);
		
		processor.processItem(conflict);
		
		ArgumentCaptor<ConflictResolution> argCaptor = ArgumentCaptor.forClass(ConflictResolution.class);
		Mockito.verify(mockConflictService).resolve(argCaptor.capture());
		Assert.assertEquals(ResolutionDecision.SYNC_NEW, argCaptor.getValue().getDecision());
		Assert.assertEquals(conflict, argCaptor.getValue().getConflict());
	}
	
}
