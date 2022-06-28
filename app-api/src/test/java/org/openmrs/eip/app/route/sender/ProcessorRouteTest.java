package org.openmrs.eip.app.route.sender;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.route.sender.SenderTestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DESTINATIONS;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_IS_SUBCLASS;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_ROUTE_RETRY_COUNT_MAP;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DB_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DB_EVENT_PROCESSOR;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.component.entity.Event;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:openmrs_core_data.sql")
@Sql(scripts = "classpath:openmrs_patient.sql")
@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_DB_EVENT_PROCESSOR + "=DEBUG")
@TestPropertySource(properties = "db-event.destinations=" + ROUTE_ID_DBSYNC)
public class ProcessorRouteTest extends BaseSenderRouteTest {
	
	private static final String ROUTE_ID_TEST = "test";
	
	@EndpointInject("mock:" + ROUTE_ID_DBSYNC)
	private MockEndpoint mockDbSyncEndpoint;
	
	@EndpointInject("mock:" + ROUTE_ID_TEST)
	private MockEndpoint mockTestEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "processor-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockDbSyncEndpoint.reset();
		mockTestEndpoint.reset();
		advise(ROUTE_ID_DB_EVENT_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint("direct:" + ROUTE_ID_DBSYNC).skipSendToOriginalEndpoint().to(mockDbSyncEndpoint);
				interceptSendToEndpoint("direct:" + ROUTE_ID_TEST).skipSendToOriginalEndpoint().to(mockTestEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldFailIfAnEntityHasRetryItemsForTheSameDestinationRoute() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(createEvent("person", "1", null, null));
		final int retryCount = 4;
		exchange.setProperty(EX_PROP_ROUTE_RETRY_COUNT_MAP, singletonMap(ROUTE_ID_DBSYNC, retryCount));
		mockDbSyncEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertEquals("Cannot process the event because the entity has " + retryCount + " event(s) in the retry queue",
		    getErrorMessage(exchange));
	}
	
	@Test
	public void shouldProcessAnEventForARow() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long debeziumEventId = 1L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		assertNotNull(debeziumEvent);
		Event event = debeziumEvent.getEvent();
		exchange.getIn().setBody(event);
		exchange.setProperty(EX_PROP_EVENT, event);
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
	@Test
	public void shouldProcessAnEventForARowWithRetryItemsForADifferentDestinationRoute() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_ROUTE_RETRY_COUNT_MAP, singletonMap("some-route", 1));
		final Long debeziumEventId = 1L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		assertNotNull(debeziumEvent);
		Event event = debeziumEvent.getEvent();
		exchange.getIn().setBody(event);
		exchange.setProperty(EX_PROP_EVENT, event);
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
	@Test
	public void shouldProcessForARowForASubclassEntityAndSetTheUuidIfMissing() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long debeziumEventId = 2L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		assertNotNull(debeziumEvent);
		Event event = debeziumEvent.getEvent();
		assertNull(event.getIdentifier());
		exchange.getIn().setBody(event);
		exchange.setProperty(EX_PROP_EVENT, event);
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_IS_SUBCLASS, true);
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertEquals("abfd940e-32dc-491f-8038-a8f3afe3e35b", event.getIdentifier());
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
	@Test
	public void shouldDeleteTheRetryItemInCaseOfARetryEvent() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long retryItemId = 1L;
		SenderRetryQueueItem retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem);
		Event event = retryItem.getEvent();
		exchange.getIn().setBody(event);
		exchange.setProperty(EX_PROP_EVENT, event);
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItem.getId());
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		assertNull(getEntity(SenderRetryQueueItem.class, retryItemId));
	}
	
	@Test
	public void shouldProcessAndNotifyAllRegisteredDestinationRoutes() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long debeziumEventId = 1L;
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		assertNotNull(debeziumEvent);
		Event event = debeziumEvent.getEvent();
		exchange.getIn().setBody(event);
		exchange.setProperty(EX_PROP_EVENT, event);
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_DESTINATIONS, ROUTE_ID_DBSYNC + ", " + ROUTE_ID_TEST);
		mockDbSyncEndpoint.expectedMessageCount(1);
		mockDbSyncEndpoint.expectedBodiesReceived(event);
		mockTestEndpoint.expectedMessageCount(1);
		mockTestEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(URI_DB_EVENT_PROCESSOR, exchange);
		
		mockDbSyncEndpoint.assertIsSatisfied();
		mockTestEndpoint.assertIsSatisfied();
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
	}
	
}
