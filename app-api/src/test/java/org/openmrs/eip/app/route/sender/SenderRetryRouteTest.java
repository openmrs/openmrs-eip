package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_FAILED_ENTITIES;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DB_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_RETRY;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DB_EVENT_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_RETRY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.exception.EIPException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_RETRY + "=DEBUG")
public class SenderRetryRouteTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_DB_EVENT_PROCESSOR)
	private MockEndpoint mockEventProcessorEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_RETRY;
	}
	
	@Before
	public void setup() throws Exception {
		mockEventProcessorEndpoint.reset();
		advise(ROUTE_ID_RETRY, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_DB_EVENT_PROCESSOR).skipSendToOriginalEndpoint().to(mockEventProcessorEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldNotCallTheDebeziumEventProcessorIfNoRetriesExists() throws Exception {
		mockEventProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_RETRY, new DefaultExchange(camelContext));
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "No events found in the retry queue");
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
	public void shouldLoadAllRetryItemsSortedByDateCreatedAndCallTheEventProcessorForEach() throws Exception {
		final int retryCount = 4;
		List<SenderRetryQueueItem> retries = TestUtils.getEntities(SenderRetryQueueItem.class);
		for (SenderRetryQueueItem retry : retries) {
			assertEquals(1, retry.getAttemptCount().intValue());
		}
		assertEquals(retryCount, retries.size());
		DefaultExchange exchange = new DefaultExchange(camelContext);
		mockEventProcessorEndpoint.expectedMessageCount(retryCount);
		List<Event> receivedBodies = new ArrayList();
		List<Event> receivedEvents = new ArrayList();
		List<Long> receivedRetryItemIds = new ArrayList();
		List<SenderRetryQueueItem> receivedRetryItems = new ArrayList();
		mockEventProcessorEndpoint.whenAnyExchangeReceived(e -> {
			receivedBodies.add(e.getIn().getBody(Event.class));
			receivedEvents.add(e.getProperty(EX_PROP_EVENT, Event.class));
			receivedRetryItemIds.add(e.getProperty(EX_PROP_RETRY_ITEM_ID, Long.class));
			receivedRetryItems.add(e.getProperty(EX_PROP_RETRY_ITEM, SenderRetryQueueItem.class));
		});
		
		producerTemplate.send(URI_RETRY, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Event count in the retry queue: " + retries.size());
		assertEquals(retryCount, receivedEvents.size());
		assertEquals(retryCount, receivedBodies.size());
		assertEquals(retryCount, receivedRetryItemIds.size());
		assertEquals(retryCount, receivedRetryItems.size());
		assertEquals(4L, receivedRetryItemIds.get(0).longValue());
		assertEquals(1L, receivedRetryItemIds.get(1).longValue());
		assertEquals(2L, receivedRetryItemIds.get(2).longValue());
		assertEquals(3L, receivedRetryItemIds.get(3).longValue());
		for (SenderRetryQueueItem retry : receivedRetryItems) {
			assertEquals(2, retry.getAttemptCount().intValue());
		}
		Assert.assertTrue(TestUtils.getEntities(SenderRetryQueueItem.class).isEmpty());
		Assert.assertTrue(exchange.getProperty(EX_PROP_FAILED_ENTITIES, List.class).isEmpty());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
	public void shouldFailIfAnEntityAlreadyHasAFailedRetryItemInTheCurrentIteration() throws Exception {
		List<SenderRetryQueueItem> retries = TestUtils.getEntities(SenderRetryQueueItem.class);
		assertEquals(4, retries.size());
		List<Exchange> failedExchanges = new ArrayList();
		advise(ROUTE_ID_RETRY, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(TestConstants.URI_ERROR_HANDLER).process(e -> failedExchanges.add(e));
			}
			
		});
		
		DefaultExchange exchange = new DefaultExchange(camelContext);
		mockEventProcessorEndpoint.expectedMessageCount(2);
		mockEventProcessorEndpoint.whenExchangeReceived(2, e -> {
			exchange.getProperty(EX_PROP_FAILED_ENTITIES, Set.class).addAll(Arrays.asList("person#1", "patient#1"));
		});
		
		producerTemplate.send(URI_RETRY, exchange);
		
		mockEventProcessorEndpoint.assertIsSatisfied();
		Assert.assertEquals(2, failedExchanges.size());
		for (Exchange e : failedExchanges) {
			assertEquals("Skipped because the entity had older failed event(s) in the queue", getErrorMessage(e));
			assertEquals(2, e.getProperty(EX_PROP_RETRY_ITEM, SenderRetryQueueItem.class).getAttemptCount().intValue());
			assertEquals(2, e.getProperty(EX_PROP_RETRY_ITEM, SenderRetryQueueItem.class).getAttemptCount().intValue());
		}
		retries = TestUtils.getEntities(SenderRetryQueueItem.class);
		assertEquals(2, retries.size());
		assertEquals(2, retries.get(0).getId().longValue());
		assertEquals(3, retries.get(1).getId().longValue());
	}
	
}
