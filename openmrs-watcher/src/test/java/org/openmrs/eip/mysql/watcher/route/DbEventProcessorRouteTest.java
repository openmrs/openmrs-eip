package org.openmrs.eip.mysql.watcher.route;

import static java.util.Collections.singletonMap;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_ERROR_HANDLER;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.Constants;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.TestOpenmrsDataSourceConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Import(TestOpenmrsDataSourceConfig.class)
@TestPropertySource(properties = PROP_URI_ERROR_HANDLER + "=" + TestConstants.URI_TEST_ERROR_HANDLER)
@TestPropertySource(properties = "camel.springboot.xml-routes=classpath:camel/db-event-processor.xml")
@TestPropertySource(properties = "db-event.destinations=" + DbEventProcessorRouteTest.ROUTE_URI_LISTENER)
@TestPropertySource(properties = "eip.watchedTables=orders")
@Sql(value = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = Constants.MGT_DATASOURCE_NAME, transactionManager = Constants.MGT_TX_MGR_NAME))
@Sql(value = "classpath:openmrs_orders.sql", config = @SqlConfig(dataSource = Constants.OPENMRS_DATASOURCE_NAME, transactionManager = "openmrsTestTxManager"))
public class DbEventProcessorRouteTest extends BaseWatcherRouteTest {
	
	private static final String ROUTE_ID = "db-event-processor";
	
	private static final String ROUTE_URI = "direct:" + ROUTE_ID;
	
	protected static final String ROUTE_URI_LISTENER = "mock:event-listener";
	
	private static final String ORDER_UUID = "order_uuid";
	
	private static final String PREV_COLUMN = "previous_order";
	
	private static final String ORDER_ID_COLUMN = "order_id";
	
	private static final Integer PREV_ORDER_ID = 1;
	
	private static final String ERR_MSG = "Moving order event to the failure queue because its previous order has 1 event(s) in the retry queue for route: "
	        + ROUTE_URI_LISTENER;
	
	private static final String END_ROUTE_MSG = "Done processing db event";
	
	@EndpointInject(ROUTE_URI_LISTENER)
	private MockEndpoint mockEventListenerEndpoint;
	
	@Before
	public void setup() {
		mockEventListenerEndpoint.reset();
	}
	
	@Test
	public void shouldNotProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheRoute()
	    throws Exception {
		Event event = createEvent("orders", "2", ORDER_UUID, "c");
		event.setCurrentState(singletonMap(PREV_COLUMN, PREV_ORDER_ID));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessATestOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheRoute()
	    throws Exception {
		final Integer orderId = 102;
		Event event = createEvent("test_order", orderId.toString(), ORDER_UUID, "c");
		event.setCurrentState(singletonMap(ORDER_ID_COLUMN, orderId));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessADrugOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheRoute()
	    throws Exception {
		final Integer orderId = 104;
		Event event = createEvent("drug_order", orderId.toString(), ORDER_UUID, "c");
		event.setCurrentState(singletonMap(ORDER_ID_COLUMN, orderId));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldNotProcessADeletedOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheRoute()
	    throws Exception {
		Event event = createEvent("orders", "2", ORDER_UUID, "d");
		event.setPreviousState(singletonMap(PREV_COLUMN, PREV_ORDER_ID));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, ERR_MSG);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldProcessAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForAnotherRoute()
	    throws Exception {
		Event event = createEvent("orders", "5", ORDER_UUID, "c");
		event.setCurrentState(singletonMap(PREV_COLUMN, "2"));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "Publishing to destination: " + ROUTE_URI_LISTENER);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
	@Test
	public void shouldProcessARetryItemForAnOrderThatHasAPreviousOrderIfThePreviousOrderIsInTheErrorQueueForTheRoute()
	    throws Exception {
		Event event = createEvent("orders", "2", ORDER_UUID, "c");
		event.setCurrentState(singletonMap(PREV_COLUMN, PREV_ORDER_ID));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty("retry-item-id", 5);
		exchange.setProperty(PROP_EVENT, event);
		
		producerTemplate.send(ROUTE_URI, exchange);
		
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "Publishing to destination: " + ROUTE_URI_LISTENER);
		assertMessageLogged(Level.DEBUG, END_ROUTE_MSG);
	}
	
}
