package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.route.sender.DebeziumEventProcessorRouteTest.DEST_DB_SYNC;
import static org.openmrs.eip.app.route.sender.DebeziumEventProcessorRouteTest.DEST_SENAITE;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_IS_SUBCLASS;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_ROUTE_RETRY_COUNT_MAP;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBZM_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DB_EVENT_PROCESSOR_ROUTE;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DB_EVENT_PROCESSOR;

import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.sender.SenderConstants;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@TestPropertySource(properties = "logging.level.debezium-event-processor=DEBUG")
@TestPropertySource(properties = "db-event.destinations=" + DEST_DB_SYNC + ", " + DEST_SENAITE)
public class DebeziumEventProcessorRouteTest extends BaseSenderRouteTest {
	
	protected static final String DEST_DB_SYNC = "direct:db-sync";
	
	protected static final String DEST_SENAITE = "direct:senaite";
	
	@EndpointInject("mock:" + ROUTE_ID_DB_EVENT_PROCESSOR_ROUTE)
	private MockEndpoint mockEventProcessorEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "debezium-event-processor-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockEventProcessorEndpoint.reset();
		advise(ROUTE_ID_DBZM_EVENT_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_DB_EVENT_PROCESSOR).skipSendToOriginalEndpoint().to(mockEventProcessorEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldFailIfARetryItemForTheEntityIsForAnInvalidRoute() throws Exception {
		final String table = "person";
		final String id = "2";
		final String destination = "direct:invalid-dest";
		assertTrue(SenderTestUtils.hasRetryItem(table, id, destination));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(createDebeziumEvent(table, id, null, "u"));
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals("No listener route found with name " + destination, getErrorMessage(exchange));
	}
	
	@Test
	public void shouldProcessAnEventForAnEntityWithNoRetryItems() throws Exception {
		final String table = "person";
		final String id = "3";
		assertFalse(SenderTestUtils.hasRetryItem(table, id, DEST_DB_SYNC));
		Exchange exchange = new DefaultExchange(camelContext);
		DebeziumEvent debeziumEvent = createDebeziumEvent(table, id, null, "u");
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(debeziumEvent.getEvent(), exchange.getProperty(SenderConstants.EX_PROP_EVENT));
		assertEquals(debeziumEvent.getEvent(), exchange.getIn().getBody());
		assertFalse(exchange.getProperty(EX_PROP_IS_SUBCLASS, Boolean.class));
		Map<String, Integer> routRetryCountMap = exchange.getProperty(EX_PROP_ROUTE_RETRY_COUNT_MAP, Map.class);
		assertEquals(2, routRetryCountMap.size());
		assertEquals(0, routRetryCountMap.get(DEST_DB_SYNC).intValue());
		assertEquals(0, routRetryCountMap.get(DEST_SENAITE).intValue());
	}
	
	@Test
	public void shouldProcessAnEventForAnEntityWithRetryItems() throws Exception {
		final String table = "person";
		final String id = "1";
		assertTrue(SenderTestUtils.hasRetryItem(table, id, DEST_DB_SYNC));
		Exchange exchange = new DefaultExchange(camelContext);
		DebeziumEvent debeziumEvent = createDebeziumEvent(table, id, null, "u");
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(debeziumEvent.getEvent(), exchange.getProperty(SenderConstants.EX_PROP_EVENT));
		assertEquals(debeziumEvent.getEvent(), exchange.getIn().getBody());
		assertFalse(exchange.getProperty(EX_PROP_IS_SUBCLASS, Boolean.class));
		Map<String, Integer> routRetryCountMap = exchange.getProperty(EX_PROP_ROUTE_RETRY_COUNT_MAP, Map.class);
		assertEquals(2, routRetryCountMap.size());
		assertEquals(3, routRetryCountMap.get(DEST_DB_SYNC).intValue());
		assertEquals(1, routRetryCountMap.get(DEST_SENAITE).intValue());
	}
	
	@Test
	public void shouldProcessAnEventForASubclassEntity() throws Exception {
		final String table = "patient";
		final String id = "1";
		assertTrue(SenderTestUtils.hasRetryItem(table, id, DEST_DB_SYNC));
		Exchange exchange = new DefaultExchange(camelContext);
		DebeziumEvent debeziumEvent = createDebeziumEvent(table, id, null, "u");
		exchange.getIn().setBody(debeziumEvent);
		mockEventProcessorEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(URI_DBZM_EVENT_PROCESSOR, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertEquals(debeziumEvent.getEvent(), exchange.getProperty(SenderConstants.EX_PROP_EVENT));
		assertEquals(debeziumEvent.getEvent(), exchange.getIn().getBody());
		assertTrue(exchange.getProperty(EX_PROP_IS_SUBCLASS, Boolean.class));
		Map<String, Integer> routRetryCountMap = exchange.getProperty(EX_PROP_ROUTE_RETRY_COUNT_MAP, Map.class);
		assertEquals(2, routRetryCountMap.size());
		assertEquals(3, routRetryCountMap.get(DEST_DB_SYNC).intValue());
		assertEquals(1, routRetryCountMap.get(DEST_SENAITE).intValue());
	}
	
}
