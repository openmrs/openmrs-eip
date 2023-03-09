package org.openmrs.eip.app;

import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.powermock.reflect.Whitebox;

public class BaseFromCamelToCamelEndpointProcessorTest {
	
	private static final String MOCK_URI = "mock:uri";
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	public class TestProcessor extends BaseFromCamelToCamelEndpointProcessor<SyncedMessage> {
		
		public TestProcessor(String endpointUri, ProducerTemplate producerTemplate) {
			super(endpointUri, producerTemplate, null);
		}
		
		@Override
		public String getProcessorName() {
			return null;
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
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldSendTheItemToTheEndpointUri() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		TestProcessor processor = new TestProcessor(MOCK_URI, mockProducerTemplate);
		Whitebox.setInternalState(processor, ProducerTemplate.class, mockProducerTemplate);
		SyncedMessage msg = new SyncedMessage();
		
		processor.processItem(msg);
		
		Mockito.verify(mockProducerTemplate).sendBody(MOCK_URI, msg);
	}
	
}
