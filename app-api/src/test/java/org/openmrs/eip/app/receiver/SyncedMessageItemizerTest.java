package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.PageRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
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
		PowerMockito.mockStatic(SyncContext.class);
		itemizer = new SyncedMessageItemizer(mockSite);
		Whitebox.setInternalState(itemizer, SyncedMessageRepository.class, mockRepo);
		Whitebox.setInternalState(itemizer, SyncedMessageItemizingProcessor.class, mockProcessor);
	}
	
	@Test
	public void doRun_shouldLoadUnItemizedMessagesAndInvokeTheProcessor() throws Exception {
		List<SyncedMessage> msgs = Collections.singletonList(new SyncedMessage());
		when(mockRepo.getBatchOfUnItemizedMessages(mockSite, PageRequest.of(0, 1000))).thenReturn(msgs);
		
		Assert.assertFalse(itemizer.doRun());
		
		Mockito.verify(mockProcessor).processWork(msgs);
	}
	
	@Test
	public void doRun_shouldNotInvokeTheProcessorIfThereAreNoUnItemizedMessages() throws Exception {
		when(mockRepo.getBatchOfUnItemizedMessages(mockSite, PageRequest.of(0, 1000))).thenReturn(Collections.emptyList());
		
		Assert.assertTrue(itemizer.doRun());
		
		Mockito.verify(mockProcessor, never()).processWork(anyList());
	}
	
}
