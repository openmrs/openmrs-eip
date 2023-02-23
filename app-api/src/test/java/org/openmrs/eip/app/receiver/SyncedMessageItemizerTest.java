package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.powermock.reflect.Whitebox;

public class SyncedMessageItemizerTest {
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private SyncedMessageRepository mockRepo;
	
	@Mock
	private SyncedMessageItemizingProcessor mockProcessor;
	
	private SyncedMessageItemizer itemizer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		itemizer = new SyncedMessageItemizer(mockSite);
		Whitebox.setInternalState(itemizer, SyncedMessageRepository.class, mockRepo);
		Whitebox.setInternalState(itemizer, SyncedMessageItemizingProcessor.class, mockProcessor);
	}
	
	@Test
	public void doRun_shouldLoadUnItemizedMessagesAndInvokeTheProcessor() throws Exception {
		List<SyncedMessage> msgs = Collections.singletonList(new SyncedMessage());
		when(mockRepo.findFirst1000BySiteAndItemizedOrderByDateCreatedAscIdAsc(mockSite, false)).thenReturn(msgs);
		
		Assert.assertFalse(itemizer.doRun());
		
		Mockito.verify(mockProcessor).processWork(msgs);
	}
	
	@Test
	public void doRun_shouldNotInvokeTheProcessorIfThereAreNoUnItemizedMessages() throws Exception {
		when(mockRepo.findFirst1000BySiteAndItemizedOrderByDateCreatedAscIdAsc(mockSite, false))
		        .thenReturn(Collections.emptyList());
		
		Assert.assertTrue(itemizer.doRun());
		
		Mockito.verify(mockProcessor, never()).processWork(anyList());
	}
	
}
