package org.openmrs.eip.app;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class BaseDelegatingQueueTaskTest {
	
	private static class MockTask extends BaseDelegatingQueueTask {
		
		public MockTask(BaseQueueProcessor processor) {
			super(processor);
		}
		
		@Override
		public List getNextBatch() {
			return null;
		}
		
		@Override
		public String getTaskName() {
			return null;
		}
	}
	
	@Test
	public void process_shouldCallTheProcessor() throws Exception {
		BaseQueueProcessor mockProcessor = Mockito.mock(BaseQueueProcessor.class);
		MockTask task = new MockTask(mockProcessor);
		List mockList = Mockito.mock(List.class);
		
		task.process(mockList);
		
		Mockito.verify(mockProcessor).processWork(mockList);
	}
	
}
