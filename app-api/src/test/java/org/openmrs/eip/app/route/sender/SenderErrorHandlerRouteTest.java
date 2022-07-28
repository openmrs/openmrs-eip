package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_FAILED_ENTITIES;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_ERROR_HANDLER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_ERROR_HANDLER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.exception.EIPException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import io.debezium.DebeziumException;

@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@Sql(scripts = "classpath:mgt_sender_retry_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ERROR_HANDLER + "=DEBUG")
public class SenderErrorHandlerRouteTest extends BaseSenderRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return "error-handler-route";
	}
	
	@Before
	public void setup() throws Exception {
		advise(ROUTE_ID_ERROR_HANDLER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(URI_ERROR_HANDLER);
			}
			
		});
	}
	
	@Test
	public void shouldAddTheEventToTheErrorQueue() {
		final Long debeziumEventId = 1L;
		final String errorMsg = "test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
		List<SenderRetryQueueItem> errorItems = TestUtils.getEntities(SenderRetryQueueItem.class).stream()
		        .filter(r -> r.getEvent().getTableName().equals(event.getTableName())
		                && r.getEvent().getPrimaryKeyId().equals(event.getPrimaryKeyId()))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		SenderRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(event.getIdentifier(), errorItem.getEvent().getIdentifier());
		assertEquals(event.getOperation(), errorItem.getEvent().getOperation());
		assertEquals(event.getSnapshot(), errorItem.getEvent().getSnapshot());
		assertEquals(event.getRequestUuid(), errorItem.getEvent().getRequestUuid());
		assertEquals(1, errorItem.getAttemptCount().intValue());
		assertNotNull(errorItem.getDateCreated());
		assertNull(errorItem.getDateChanged());
		assertEquals(EIPException.class.getName(), errorItem.getExceptionType());
		assertEquals(errorMsg, errorItem.getMessage());
	}
	
	@Test
	public void shouldSetExceptionTypeToRootCause() {
		final Long debeziumEventId = 1L;
		final String rootCauseMsg = "test root error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException("test1", new Exception("test2", new DebeziumException(rootCauseMsg))));
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<SenderRetryQueueItem> errorItems = TestUtils.getEntities(SenderRetryQueueItem.class).stream()
		        .filter(r -> r.getEvent().getTableName().equals(event.getTableName())
		                && r.getEvent().getPrimaryKeyId().equals(event.getPrimaryKeyId()))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		SenderRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(DebeziumException.class.getName(), errorItem.getExceptionType());
		assertEquals(rootCauseMsg, errorItem.getMessage());
		
	}
	
	@Test
	public void shouldProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		SenderRetryQueueItem retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(SenderRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retryItem);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(SenderRetryQueueItem.class).size());
		retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
		Set<String> failedEntities = exchange.getProperty(EX_PROP_FAILED_ENTITIES, Set.class);
		assertEquals(2, failedEntities.size());
		Event event = retryItem.getEvent();
		assertTrue(failedEntities.contains(event.getTableName() + "#" + event.getPrimaryKeyId()));
		assertTrue(failedEntities.contains("patient#" + event.getPrimaryKeyId()));
	}
	
	@Test
	public void shouldTruncateTheErrorMessageIfItIsLongerThan1024Characters() {
		final Long debeziumEventId = 1L;
		final String errorMsg = RandomStringUtils.randomAscii(1025);
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<SenderRetryQueueItem> errorItems = TestUtils.getEntities(SenderRetryQueueItem.class).stream()
		        .filter(r -> r.getEvent().getTableName().equals(event.getTableName())
		                && r.getEvent().getPrimaryKeyId().equals(event.getPrimaryKeyId()))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		SenderRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(EIPException.class.getName(), errorItem.getExceptionType());
		assertEquals(errorMsg.substring(0, 1024), errorItem.getMessage());
		
	}
	
	@Test
	public void shouldFailIfNoDebeziumEventOrRetryItemIdOrRetryItemExistOnTheExchange() {
		final Long retryItemId = 1L;
		DefaultExchange exchange = new DefaultExchange(camelContext);
		SenderRetryQueueItem retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertNotNull(getEntity(SenderRetryQueueItem.class, retryItemId));
		assertEquals(EIPException.class, exchange.getException().getClass());
		assertEquals("Not enough details to handle the exception", exchange.getException().getMessage());
	}
	
	@Test
	public void shouldLoadTheRetryItemIfNotSetOnTheExchangeWhenProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		SenderRetryQueueItem retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(SenderRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(SenderRetryQueueItem.class).size());
		retryItem = getEntity(SenderRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
	}
	
}
