package org.openmrs.eip.mysql.watcher.route;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.Constants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.Constants.MGT_TX_MGR_NAME;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_EVENT_PROCESSOR;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.Constants;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.TestOpenmrsDataSourceConfig;
import org.openmrs.eip.mysql.watcher.management.entity.SenderRetryQueueItem;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Import(TestOpenmrsDataSourceConfig.class)
@TestPropertySource(properties = "camel.springboot.xml-routes=classpath:camel/" + WatcherRetryItemHandlerRouteTest.ROUTE_ID
        + ".xml")
@TestPropertySource(properties = "db-event.destinations=" + WatcherRetryItemHandlerRouteTest.MOCK_LISTENER)
@TestPropertySource(properties = PROP_URI_EVENT_PROCESSOR + "=" + WatcherRetryItemHandlerRouteTest.ROUTE_URI_PROCESSOR)
@TestPropertySource(properties = "eip.watchedTables=orders")
@Sql(value = "classpath:openmrs_orders.sql", config = @SqlConfig(dataSource = Constants.OPENMRS_DATASOURCE_NAME, transactionManager = "openmrsTestTxManager"))
@Sql(value = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR_NAME))
public class WatcherRetryItemHandlerRouteTest extends BaseWatcherRouteTest {
	
	protected static final String ROUTE_ID = "watcher-retry-item-handler";
	
	private static final String ROUTE_URI = "direct:" + ROUTE_ID;
	
	protected static final String ROUTE_URI_PROCESSOR = "mock:event-processor";
	
	protected static final String MOCK_LISTENER = "mock:event-listener";
	
	private static final String EX_PROP_FAILURES = "route-failed-entities";
	
	@EndpointInject(ROUTE_URI_PROCESSOR)
	private MockEndpoint mockEventProcessorEndpoint;
	
	private static final String EX_MSG = "Skipped because its previous order had older failed event(s) in the queue";
	
	@Before
	public void setup() {
		mockEventProcessorEndpoint.reset();
	}
	
	private SenderRetryQueueItem addRetryItem(String entityTable, String entityId, String entityUuid, String route) {
		SenderRetryQueueItem r = new SenderRetryQueueItem();
		r.setEvent(createEvent(entityTable, entityId, entityUuid, "c"));
		r.setRoute(route);
		r.setExceptionType(EIPException.class.getName());
		r.setDateCreated(new Date());
		return (SenderRetryQueueItem) producerTemplate.requestBody("jpa:" + SenderRetryQueueItem.class.getSimpleName(), r);
	}
	
	@Test
	public void shouldProcessARetryItem() throws Exception {
		SenderRetryQueueItem retry = addRetryItem("person", "1", null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, emptyList());
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(1);
		final String exPropRetryItemId = "retry-item-id";
		final String exPropRetryItem = "retry-item";
		final String exPropEvent = "event";
		final String exPropDestinations = "db-event-destinations";
		assertNull(exchange.getProperty(exPropRetryItemId));
		assertNull(exchange.getProperty(exPropRetryItem));
		assertNull(exchange.getProperty(exPropEvent));
		assertEquals(1, retry.getAttemptCount().intValue());
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(retry.getId(), exchange.getProperty(exPropRetryItemId));
		assertEquals(retry, exchange.getProperty(exPropRetryItem));
		assertEquals(MOCK_LISTENER, exchange.getProperty(exPropDestinations));
		retry = exchange.getProperty(exPropRetryItem, SenderRetryQueueItem.class);
		assertEquals(2, retry.getAttemptCount().intValue());
		assertEquals(retry.getEvent(), exchange.getProperty(exPropEvent));
		assertEquals(retry.getEvent(), exchange.getIn().getBody(Event.class));
	}
	
	@Test
	public void shouldFailIfAnOrderHasAPreviousOrderAndThePreviousOrderIsAmongFailedRetriesForTheRoute() throws Exception {
		final String tableName = "orders";
		SenderRetryQueueItem retry = addRetryItem(tableName, "102", null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#101#" + MOCK_LISTENER));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(EX_MSG, getErrorMessage(exchange));
	}
	
	@Test
	public void shouldFailIfATestOrderHasAPreviousOrderAndThePreviousOrderIsAmongFailedRetriesForTheRoute()
	    throws Exception {
		final String tableName = "test_order";
		SenderRetryQueueItem retry = addRetryItem(tableName, "102", null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#101#" + MOCK_LISTENER));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(EX_MSG, getErrorMessage(exchange));
	}
	
	@Test
	public void shouldFailIfADrugOrderHasAPreviousOrderAndThePreviousOrderIsAmongFailedRetriesForTheRoute()
	    throws Exception {
		final String tableName = "drug_order";
		SenderRetryQueueItem retry = addRetryItem(tableName, "102", null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#101#" + MOCK_LISTENER));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(EX_MSG, getErrorMessage(exchange));
	}
	
	@Test
	public void shouldProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsAmongFailedRetriesButForAnotherRoute()
	    throws Exception {
		final String tableName = "orders";
		SenderRetryQueueItem retry = addRetryItem(tableName, "102", null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#101#mock:other"));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldFailIfAnEntityHasAnEventAmongFailedRetriesForTheRoute() throws Exception {
		final String tableName = "person";
		final String personId = "1";
		SenderRetryQueueItem retry = addRetryItem(tableName, personId, null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#" + personId + "#" + MOCK_LISTENER));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Skipping retry item with id: " + retry.getId() + " for " + tableName + "#"
		        + personId + " because it still has older failed event(s) in the queue for route: " + MOCK_LISTENER);
		assertEquals("Skipped because the entity had older failed event(s) in the queue", getErrorMessage(exchange));
	}
	
	@Test
	public void shouldFailIfTheEntityRouteIsNotAmongTheRegisteredDestinations() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty("event-destinations", emptyList());
		exchange.getIn().setBody(3);
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals("No listener route found with name direct:invalid-dest", getErrorMessage(exchange));
	}
	
	@Test
	public void shouldProcessAnEntityThatHasAnEventAmongFailedRetriesButForAnotherRoute() throws Exception {
		final String tableName = "person";
		final String personId = "1";
		SenderRetryQueueItem retry = addRetryItem(tableName, personId, null, MOCK_LISTENER);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_FAILURES, singletonList(tableName + "#" + personId + "#mock:other"));
		exchange.getIn().setBody(retry.getId());
		mockEventProcessorEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
	}
	
}
