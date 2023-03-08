package org.openmrs.eip.app;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.powermock.reflect.Whitebox;

public class BaseFromCamelProcessorTest {
	
	public class TestProcessor extends BaseFromCamelProcessor<SyncedMessage> {
		
		List<SyncedMessage> processedItems;
		
		public TestProcessor(ThreadPoolExecutor executor) {
			super(executor);
		}
		
		@Override
		public void processWork(List<SyncedMessage> items) {
			processedItems = items;
		}
		
		@Override
		public String getProcessorName() {
			return null;
		}
		
		@Override
		public void processItem(SyncedMessage item) {
		}
		
		@Override
		public String getUniqueId(SyncedMessage item) {
			return null;
		}
		
		@Override
		public String getQueueName() {
			return null;
		}
		
		@Override
		public String getThreadName(SyncedMessage item) {
			return null;
		}
		
		@Override
		public String getLogicalType(SyncedMessage item) {
			return null;
		}
		
		@Override
		public List<String> getLogicalTypeHierarchy(String logicalType) {
			return null;
		}
		
	}
	
	@Test
	public void process_shouldCallProcessWork() throws Exception {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		TestProcessor processor = new TestProcessor(null);
		Assert.assertNull(processor.processedItems);
		List<SyncedMessage> items = Collections.singletonList(new SyncedMessage());
		
		processor.process(ExchangeBuilder.anExchange(new DefaultCamelContext()).withBody(items).build());
		
		Assert.assertEquals(items, processor.processedItems);
	}
	
}
