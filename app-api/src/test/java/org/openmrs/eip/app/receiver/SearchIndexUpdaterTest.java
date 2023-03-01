package org.openmrs.eip.app.receiver;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SearchIndexUpdaterTest {
	
	@Mock
	private SearchIndexUpdatingProcessor mockProcessor;
	
	private SearchIndexUpdater updater;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		updater = new SearchIndexUpdater(null);
		Whitebox.setInternalState(updater, SearchIndexUpdatingProcessor.class, mockProcessor);
	}
	
	@Test
	public void process() throws Exception {
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		
		updater.process(actions);
		
		Mockito.verify(mockProcessor).processWork(actions);
	}
	
}
