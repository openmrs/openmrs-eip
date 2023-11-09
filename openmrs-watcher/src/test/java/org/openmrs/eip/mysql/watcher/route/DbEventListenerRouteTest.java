package org.openmrs.eip.mysql.watcher.route;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT_DESTINATIONS;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_ERROR_HANDLER;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_EVENT_PROCESSOR;
import static org.openmrs.eip.mysql.watcher.WatcherTestConstants.URI_MOCK_ERROR_HANDLER;
import static org.openmrs.eip.mysql.watcher.WatcherTestConstants.URI_MOCK_EVENT_PROCESSOR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.management.entity.SenderRetryQueueItem;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Disabled
@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=false")
@TestPropertySource(properties = "eip.watchedTables=person")
@TestPropertySource(properties = "db-event.destinations=mock:db-event-processor")
@TestPropertySource(properties = PROP_URI_EVENT_PROCESSOR + "=" + URI_MOCK_EVENT_PROCESSOR)
@TestPropertySource(properties = PROP_URI_ERROR_HANDLER + "=" + URI_MOCK_ERROR_HANDLER)
@Sql(value = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = "mngtDataSource", transactionManager = "mngtTransactionManager"))
public class DbEventListenerRouteTest extends BaseWatcherRouteTest {
	
	private static final String URI = "direct:db-event-listener";
	
	private static final String TABLE_NAME = "person";
	
	private static final String ENTITY_CLASS = SenderRetryQueueItem.class.getSimpleName();
	
	@EndpointInject(URI_MOCK_ERROR_HANDLER)
	protected MockEndpoint mockErrorHandlerEndpoint;
	
	@EndpointInject(URI_MOCK_EVENT_PROCESSOR)
	private MockEndpoint mockProcessorEndpoint;
	
	@BeforeEach
	public void setup() throws Exception {
		mockProcessorEndpoint.reset();
		mockErrorHandlerEndpoint.reset();
	}
	
	private void addStandardExpectations(Event event) {
		mockProcessorEndpoint.expectedMessageCount(1);
		mockProcessorEndpoint.expectedBodiesReceived(event);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_EVENT, event);
		mockErrorHandlerEndpoint.expectedMessageCount(0);
	}
	
	@Test
	public void shouldSetTheExchangeProperties() throws Exception {
		Map<String, Object> props = new HashMap();
		props.put(PROP_EVENT_DESTINATIONS, URI_MOCK_EVENT_PROCESSOR);
		PropertySource customPropSource = new MapPropertySource("test", props);
		env.getPropertySources().addLast(customPropSource);
		loadXmlRoutesInCamelDirectory("db-event-listener.xml");
		Event event = createEvent(TABLE_NAME, "1", "person-uuid", "c");
		addStandardExpectations(event);
		Map retryCountMap = new HashMap();
		retryCountMap.put(URI_MOCK_EVENT_PROCESSOR, 0);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_RETRY_MAP, retryCountMap);
		
		producerTemplate.sendBodyAndProperty(URI, null, PROP_EVENT, event);
		
		mockProcessorEndpoint.assertIsSatisfied();
		mockErrorHandlerEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldFailIfTheEntityHasAnItemInTheErrorQueueWithAnInvalidDestination() throws Exception {
		Map<String, Object> props = new HashMap();
		props.put(PROP_EVENT_DESTINATIONS, "mock:no-where");
		PropertySource customPropSource = new MapPropertySource("test", props);
		env.getPropertySources().addLast(customPropSource);
		loadXmlRoutesInCamelDirectory("db-event-listener.xml");
		mockErrorHandlerEndpoint.expectedMessageCount(1);
		final String id = "2";
		String q = "jpa:" + ENTITY_CLASS + "?query=SELECT r from " + ENTITY_CLASS + " r WHERE r.event.tableName='"
		        + TABLE_NAME + "' AND r.event.primaryKeyId='" + id + "' AND r.route = 'direct:invalid-dest'";
		List<Map> existingFailures = on(camelContext).to(q).request(List.class);
		assertFalse(existingFailures.isEmpty());
		Event event = createEvent(TABLE_NAME, id, "person-uuid-2", "u");
		
		producerTemplate.sendBodyAndProperty(URI, null, PROP_EVENT, event);
		
		mockErrorHandlerEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldFailIfTheEntityAlreadyHasAnItemInTheErrorQueue() throws Exception {
		final String dbSyncUri = "direct:db-sync";
		final String senaiteUri = "direct:senaite";
		Map<String, Object> props = new HashMap();
		props.put(PROP_EVENT_DESTINATIONS, dbSyncUri + "," + senaiteUri);
		PropertySource customPropSource = new MapPropertySource("test", props);
		env.getPropertySources().addLast(customPropSource);
		loadXmlRoutesInCamelDirectory("db-event-listener.xml");
		
		final String id = "1";
		String q = "jpa:" + ENTITY_CLASS + "?query=SELECT r from " + ENTITY_CLASS + " r WHERE r.event.tableName='"
		        + TABLE_NAME + "' AND r.event.primaryKeyId='" + id + "' AND r.route = '" + dbSyncUri + "'";
		List<Map> existingFailures = on(camelContext).to(q).request(List.class);
		assertEquals(2, existingFailures.size());
		Event event = createEvent(TABLE_NAME, id, "person-uuid-2", "u");
		addStandardExpectations(event);
		Map retryCountMap = new HashMap();
		retryCountMap.put(dbSyncUri, 2);
		retryCountMap.put(senaiteUri, 1);
		mockProcessorEndpoint.expectedPropertyReceived(PROP_RETRY_MAP, retryCountMap);
		
		producerTemplate.sendBodyAndProperty(URI, null, PROP_EVENT, event);
		
		mockProcessorEndpoint.assertIsSatisfied();
		mockErrorHandlerEndpoint.assertIsSatisfied();
	}
	
}
