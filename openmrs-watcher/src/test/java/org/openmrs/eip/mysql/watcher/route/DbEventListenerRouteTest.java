package org.openmrs.eip.mysql.watcher.route;

import static org.openmrs.eip.mysql.watcher.WatcherTestConstants.URI_MOCK_EVENT_PROCESSOR;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.mysql.watcher.Event;

public class DbEventListenerRouteTest extends BaseWatcherRouteTest {
	
	private static final String URI = "direct:db-event-listener";
	
	@EndpointInject(URI_MOCK_EVENT_PROCESSOR)
	private MockEndpoint mockProcessorEndpoint;
	
	@Before
	public void setup() {
		mockProcessorEndpoint.reset();
	}
	
	private void addStandardExpectations(Event event) {
		mockProcessorEndpoint.expectedMessageCount(1);
		mockProcessorEndpoint.expectedBodiesReceived(event);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_EVENT, event);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_RETRY_MAP, event);
	}
	
	@Test
	public void shouldSetTheExchangeProperties() throws Exception {
		Event event = createEvent("person", "1", "person-uuid", "c");
		addStandardExpectations(event);
		Map retryCountMap = new HashMap();
		retryCountMap.put(URI_MOCK_EVENT_PROCESSOR, 0);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_RETRY_MAP, retryCountMap);
		
		producerTemplate.sendBodyAndProperty(URI, null, PROP_EVENT, event);
		
		mockProcessorEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldAddTheEntityRetryItemsForTheRoute() throws Exception {
		Event event = createEvent("person", "1", "person-uuid", "c");
		addStandardExpectations(event);
		Map retryCountMap = new HashMap();
		retryCountMap.put(URI_MOCK_EVENT_PROCESSOR, 0);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_RETRY_MAP, retryCountMap);
		
		producerTemplate.sendBodyAndProperty(URI, null, PROP_EVENT, event);
		
		mockProcessorEndpoint.assertIsSatisfied();
	}
	
}
