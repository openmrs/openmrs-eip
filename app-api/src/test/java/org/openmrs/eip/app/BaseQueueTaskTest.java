package org.openmrs.eip.app;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class BaseQueueTaskTest {
	
	private MockBaseQueueTask task;
	
	private static class MockBaseQueueTask extends BaseQueueTask {
		
		private List<Object> items;
		
		private List<Object> processedItems;
		
		public MockBaseQueueTask(List<Object> items) {
			this.items = items;
		}
		
		@Override
		public List getNextBatch() {
			return items;
		}
		
		@Override
		public void process(List items) throws Exception {
			processedItems = items;
		}
		
		@Override
		public String getTaskName() {
			return null;
		}
	}
	
	@Test
	public void doRun_shouldDoNothingIfThereNoMessagesAreFound() throws Exception {
		task = new MockBaseQueueTask(emptyList());
		
		Assert.assertTrue(task.doRun());
		
		Assert.assertNull(task.processedItems);
	}
	
	@Test
	public void doRun_shouldReadAndProcessTheNextBatchOfSyncedMessages() throws Exception {
		List items = Collections.singletonList(new Object());
		task = new MockBaseQueueTask(items);
		
		Assert.assertFalse(task.doRun());
		
		Assert.assertEquals(items, task.processedItems);
	}
}
