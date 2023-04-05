package org.openmrs.eip.app;

import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;

public class BasePureParallelQueueProcessorTest {
	
	private class MockProcessor extends BasePureParallelQueueProcessor {
		
		public MockProcessor() {
			super(null);
		}
		
		@Override
		public void processWork(Object work) throws Exception {
			
		}
		
		@Override
		public String getProcessorName() {
			return null;
		}
		
		@Override
		public void processItem(AbstractEntity item) {
			
		}
		
		@Override
		public String getQueueName() {
			return null;
		}
		
		@Override
		public String getThreadName(AbstractEntity item) {
			return null;
		}
	}
	
	private BasePureParallelQueueProcessor processor;
	
	@Before
	public void setup() {
		setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new MockProcessor();
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getUniqueId_shouldReturnTheItemId() {
		ReceiverSyncArchive archive = new ReceiverSyncArchive();
		final Long id = 2L;
		archive.setId(id);
		Assert.assertEquals(id.toString(), processor.getUniqueId(archive));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheItemClassName() {
		Assert.assertEquals(ReceiverSyncArchive.class.getName(), processor.getLogicalType(new ReceiverSyncArchive()));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnNull() {
		Assert.assertNull(processor.getLogicalTypeHierarchy(ReceiverSyncArchive.class.getName()));
	}
	
}
