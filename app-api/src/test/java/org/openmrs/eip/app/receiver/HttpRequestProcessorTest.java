package org.openmrs.eip.app.receiver;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class HttpRequestProcessorTest {
	
	class MockProcessor implements HttpRequestProcessor {
		
		@Override
		public Object convertBody(Object item) {
			return null;
		}
		
	}
	
	@Mock
	private CustomHttpClient mockClient;
	
	private HttpRequestProcessor processor;
	
	@Before
	public void setup() {
		processor = new MockProcessor();
	}
	
	@Test
	public void doSendRequest_shouldSendTheRequest() {
		final String resource = "person";
		final String payload = "{}";
		
		processor.doSendRequest(mockClient, resource, payload);
		
		Mockito.verify(mockClient).sendRequest(resource, payload);
	}
	
	@Test
	public void sendRequest_shouldConvertAndSendTheRequest() {
		final String resource = "person";
		final String payload = "{}";
		final Object item = new Object();
		processor = Mockito.spy(processor);
		Mockito.doReturn(payload).when(processor).convertBody(item);
		
		processor.sendRequest(resource, item, mockClient);
		
		Mockito.verify(mockClient).sendRequest(resource, payload);
	}
	
	@Test
	public void sendRequest_shouldConvertAndSendTheRequestForACollectionOfItems() {
		final String resource = "name";
		final String payload1 = "{1}";
		final String payload2 = "{2}";
		final Object item = new Object();
		processor = Mockito.spy(processor);
		Mockito.doReturn(List.of(payload1, payload2)).when(processor).convertBody(item);
		
		processor.sendRequest(resource, item, mockClient);
		
		Mockito.verify(mockClient).sendRequest(resource, payload1);
		Mockito.verify(mockClient).sendRequest(resource, payload2);
	}
	
}
