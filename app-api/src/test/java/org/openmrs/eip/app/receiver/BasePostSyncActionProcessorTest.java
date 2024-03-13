package org.openmrs.eip.app.receiver;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.receiver.HttpRequestProcessor.CACHE_RESOURCE;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.reflect.Whitebox;

public class BasePostSyncActionProcessorTest {
	
	private BasePostSyncActionProcessor processor;
	
	@Mock
	private CustomHttpClient mockHttpClient;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new CacheEvictingProcessor(null, mockHttpClient);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldSendTheItemToTheEndpointUri() {
		processor = Mockito.spy(processor);
		SyncedMessage msg = new SyncedMessage();
		String testJson = "{}";
		Mockito.doReturn(testJson).when(processor).convertBody(msg);
		Mockito.doNothing().when(mockHttpClient).sendRequest(CACHE_RESOURCE, testJson);
		Mockito.doNothing().when(processor).onSuccess(msg);
		
		processor.processItem(msg);
		
		verify(mockHttpClient).sendRequest(CACHE_RESOURCE, testJson);
		verify(processor).onSuccess(msg);
	}
	
	@Test
	public void processItem_shouldNotSendASquashedItem() {
		processor = Mockito.spy(processor);
		SyncedMessage msg = new SyncedMessage();
		when(processor.isSquashed(msg)).thenReturn(true);
		Mockito.doNothing().when(processor).onSuccess(msg);
		
		processor.processItem(msg);
		
		verifyNoInteractions(mockHttpClient);
		verify(processor).onSuccess(msg);
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingTheAssociatedSyncedMessageDetails() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final String siteUuid = "site-uuid";
		SyncedMessage msg = new SyncedMessage();
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
	public void getUniqueId_shouldReturnEntityIdentifier() {
		final Long id = 2L;
		SyncedMessage msg = new SyncedMessage();
		msg.setId(id);
		assertEquals(id.toString(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassNameOfTheAssociatedSyncedMessage() {
		assertEquals(SyncedMessage.class.getName(), processor.getLogicalType(new SyncedMessage()));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertNull(processor.getLogicalTypeHierarchy(PersonModel.class.getName()));
	}
	
	@Test
	public void processWork_shouldSquashEventsForTheSameEntityAndSendOnlyTheTheFirst() throws Exception {
		final String entityUuid1 = "uuid-1";
		final String personClass = PersonModel.class.getName();
		final String patientClass = PatientModel.class.getName();
		SyncedMessage msg1 = new SyncedMessage();
		msg1.setId(1L);
		msg1.setModelClassName(personClass);
		msg1.setIdentifier(entityUuid1);
		SyncedMessage msg2 = new SyncedMessage();
		msg2.setId(2L);
		msg2.setModelClassName(personClass);
		msg2.setIdentifier(entityUuid1);
		SyncedMessage msg3 = new SyncedMessage();
		msg3.setId(3L);
		msg3.setModelClassName(patientClass);
		msg3.setIdentifier(entityUuid1);
		
		SyncedMessage msg4 = new SyncedMessage();
		final String entityUuid2 = "uuid-2";
		msg4.setId(4L);
		msg4.setModelClassName(personClass);
		msg4.setIdentifier(entityUuid2);
		SyncedMessage msg5 = new SyncedMessage();
		msg5.setId(5L);
		msg5.setModelClassName(personClass);
		msg5.setIdentifier(entityUuid2);
		
		SyncedMessage msg6 = new SyncedMessage();
		msg6.setId(6L);
		msg6.setModelClassName(personClass);
		msg6.setIdentifier("uuid-3");
		
		SyncedMessage msg7 = new SyncedMessage();
		msg7.setId(7L);
		msg7.setModelClassName(personClass);
		msg7.setIdentifier(entityUuid1);
		
		SyncedMessage msg8 = new SyncedMessage();
		msg8.setId(8L);
		msg8.setModelClassName(VisitModel.class.getName());
		msg8.setIdentifier(entityUuid1);
		
		final String entityUuid4 = "uuid-4";
		SyncedMessage msg9 = new SyncedMessage();
		msg9.setId(9L);
		msg9.setModelClassName(personClass);
		msg9.setIdentifier(entityUuid4);
		SyncedMessage msg10 = new SyncedMessage();
		msg10.setId(10L);
		msg10.setModelClassName(personClass);
		msg10.setIdentifier(entityUuid4);
		SyncedMessage msg11 = new SyncedMessage();
		msg11.setId(11L);
		msg11.setModelClassName(personClass);
		msg11.setIdentifier(entityUuid4);
		msg11.setOperation(SyncOperation.d);
		
		SyncedMessage msg12 = new SyncedMessage();
		msg12.setId(12L);
		msg12.setModelClassName(personClass);
		msg12.setIdentifier("uuid-12");
		msg12.setOperation(SyncOperation.d);
		
		SyncedMessage msg13 = new SyncedMessage();
		msg13.setId(13L);
		msg13.setModelClassName(personClass);
		msg13.setIdentifier("uuid-13");
		msg13.setOperation(SyncOperation.d);
		
		final String entityUuid14 = "uuid-14";
		SyncedMessage msg14 = new SyncedMessage();
		msg14.setId(14L);
		msg14.setModelClassName(patientClass);
		msg14.setIdentifier(entityUuid14);
		msg14.setOperation(SyncOperation.d);
		
		SyncedMessage msg15 = new SyncedMessage();
		msg15.setId(15L);
		msg15.setModelClassName(VisitModel.class.getName());
		msg15.setIdentifier(entityUuid14);
		msg15.setOperation(SyncOperation.d);
		
		processor = Mockito.spy(processor);
		List<SyncedMessage> msgs = asList(msg1, msg2, msg3, msg4, msg5, msg6, msg9, msg10, msg11, msg12, msg7, msg8, msg13,
		    msg14, msg15);
		List<SyncedMessage> sentMsgs = new ArrayList();
		List<SyncedMessage> squashedMsgs = new ArrayList();
		Mockito.doAnswer(invocation -> {
			List<SyncedMessage> msgList = invocation.getArgument(0);
			msgList.forEach(m -> {
				if (!m.isEvictedFromCache()) {
					sentMsgs.add(m);
				} else {
					squashedMsgs.add(m);
				}
			});
			
			return null;
		}).when(processor).doProcessWork(anyList());
		
		processor.processWork(msgs);
		
		Mockito.verify(processor, times(2)).doProcessWork(anyList());
		assertTrue(isEqualCollection(asList(msg1, msg4, msg6, msg8, msg9, msg12, msg15), sentMsgs));
		assertTrue(isEqualCollection(asList(msg2, msg3, msg5, msg7, msg10, msg13, msg14), squashedMsgs));
	}
	
}
