package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_IS_SUBCLASS;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM_ID;
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
public class ProcessorRouteTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_DBSYNC)
	private MockEndpoint mockDbSyncEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "processor-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockDbSyncEndpoint.reset();
		advise(ROUTE_ID_DB_EVENT_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint("direct:" + ROUTE_ID_DBSYNC).skipSendToOriginalEndpoint().to(mockDbSyncEndpoint);
			}
			
		});
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
	}
	
	@Test
	public void shouldProcessAnEventForARowWithRetryItemsForADifferentDestinationRoute() throws Exception {
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
	}
	
	@Test
	public void shouldProcessForARowForASubclassEntityAndSetTheUuidIfMissing() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		final Long debeziumEventId = 3L;
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
	}
	
}
