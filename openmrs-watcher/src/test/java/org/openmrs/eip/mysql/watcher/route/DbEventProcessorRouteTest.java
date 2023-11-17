package org.openmrs.eip.mysql.watcher.route;

import static java.util.Collections.singletonMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_IGNORE_PREV_ORDER_IN_ERROR_QUEUE;
import static org.openmrs.eip.mysql.watcher.WatcherTestUtils.addRetryItem;
import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.Constants;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.WatcherTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "camel.springboot.routes-include-pattern=classpath:camel/db-event-processor.xml")
@TestPropertySource(properties = "db-event.destinations=" + DbEventProcessorRouteTest.ROUTE_URI_LISTENER)
@TestPropertySource(properties = "eip.watchedTables=orders")
@Sql(value = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = Constants.MGT_DATASOURCE_NAME, transactionManager = Constants.MGT_TX_MGR_NAME))
@Sql(value = "classpath:openmrs_orders.sql", config = @SqlConfig(dataSource = Constants.OPENMRS_DATASOURCE_NAME, transactionManager = "openmrsTestTxManager"))
public class DbEventProcessorRouteTest extends BaseWatcherRouteTest {
	
	private static final String ROUTE_ID = "db-event-processor";
	
	private static final String ROUTE_URI = "direct:" + ROUTE_ID;
	
	protected static final String ROUTE_URI_LISTENER = "mock:event-listener";
	
	private static final String ORDER_UUID = "order_uuid";
	
	private static final String ORDER_ID_COLUMN = "order_id";
	
	private static final String ERR_MSG = "Moving order event to the failure queue because its previous order has 1 event(s) in the retry queue for destination: "
	        + ROUTE_URI_LISTENER;
	
	private static final String END_ROUTE_MSG = "Done processing db event";
	
	@EndpointInject(ROUTE_URI_LISTENER)
	private MockEndpoint mockEventListenerEndpoint;
	
	@BeforeEach
	public void setup() {
		mockEventListenerEndpoint.reset();
		addInlinedPropertiesToEnvironment(env, PROP_IGNORE_PREV_ORDER_IN_ERROR_QUEUE + "=");
	}
	
	@Test
	public void shouldNotProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final String tableName = "orders";
		final Integer orderId = 2;
		final Integer previousOrderId = 1;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem(tableName, previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessAnOrderThatHasAPreviousTestOrderIfThePreviousTestOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 2;
		final Integer previousOrderId = 1;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("test_order", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("orders", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessAnOrderThatHasAPreviousDrugOrderIfThePreviousDrugOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 108;
		final Integer previousOrderId = 107;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("drug_order", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("orders", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessATestOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 102;
		final Integer previousOrderId = 101;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("orders", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("test_order", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessATestOrderThatHasAPreviousTestOrderIfThePreviousTestOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final String tableName = "test_order";
		final Integer orderId = 106;
		final Integer previousOrderId = 105;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem(tableName, previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessADrugOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 104;
		final Integer previousOrderId = 103;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("orders", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("drug_order", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessADrugOrderThatHasAPreviousDrugOrderIfThePreviousDrugOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final String tableName = "drug_order";
		final Integer orderId = 108;
		final Integer previousOrderId = 107;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem(tableName, previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "c");
		event.setCurrentState(singletonMap(ORDER_ID_COLUMN, orderId));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessADeletedOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final String tableName = "orders";
		final Integer orderId = 2;
		final Integer previousOrderId = 1;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem(tableName, previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "d");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForAnotherDestination()
	        throws Exception {
		final String tableName = "orders";
		final Integer orderId = 110;
		final Integer previousOrderId = 109;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty("retry-item-id", 1);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "Publishing to destination: " + ROUTE_URI_LISTENER);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldProcessARetryItemForAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 2;
		final Integer previousOrderId = 1;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("orders", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("orders", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty("retry-item-id", 5);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "Publishing to destination: " + ROUTE_URI_LISTENER);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestinationButTheCheckIsDisabled()
	        throws Exception {
		final Integer orderId = 2;
		final Integer previousOrderId = 1;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("orders", previousOrderId.toString(), ROUTE_URI_LISTENER));
		addInlinedPropertiesToEnvironment(env, PROP_IGNORE_PREV_ORDER_IN_ERROR_QUEUE + "=true");
		Event event = createEvent("orders", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedBodiesReceived(event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "Publishing to destination: " + ROUTE_URI_LISTENER);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessAnOrderThatHasAPreviousReferralOrderIfThePreviousReferralOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 114;
		final Integer previousOrderId = 113;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("referral_order", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("orders", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessAReferralOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final Integer orderId = 112;
		final Integer previousOrderId = 111;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem("orders", previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent("referral_order", orderId.toString(), ORDER_UUID, "c");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessAReferralOrderThatHasAPreviousReferralOrderIfThePreviousReferralOrderIsInTheErrorQueueForTheDestination()
	        throws Exception {
		final String tableName = "referral_order";
		final Integer orderId = 114;
		final Integer previousOrderId = 113;
		assertEquals(previousOrderId, WatcherTestUtils.getPreviousOrderId(orderId));
		assertTrue(WatcherTestUtils.hasRetryItem(tableName, previousOrderId.toString(), ROUTE_URI_LISTENER));
		Event event = createEvent(tableName, orderId.toString(), ORDER_UUID, "c");
		event.setCurrentState(singletonMap(ORDER_ID_COLUMN, orderId));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
}
