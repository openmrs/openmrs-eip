package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.route.sender.SenderTestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_DBZM_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.EX_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_ERROR_HANDLER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_ERROR_HANDLER;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
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
		exchange.setProperty(Exchange.FAILURE_HANDLED, true);
		exchange.setException(new EIPException(errorMsg));
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertNull(getEntity(DebeziumEvent.class, debeziumEventId));
		List<SenderRetryQueueItem> errorItems = SenderTestUtils.getEntities(SenderRetryQueueItem.class).stream()
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
		exchange.setProperty(Exchange.FAILURE_HANDLED, true);
		exchange.setException(new EIPException("test1", new Exception("test2", new DebeziumException(rootCauseMsg))));
		DebeziumEvent debeziumEvent = getEntity(DebeziumEvent.class, debeziumEventId);
		Event event = debeziumEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		exchange.setProperty(EX_PROP_DBZM_EVENT, debeziumEvent);
		exchange.setProperty(EX_PROP_EVENT, debeziumEvent.getEvent());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<SenderRetryQueueItem> errorItems = SenderTestUtils.getEntities(SenderRetryQueueItem.class).stream()
		        .filter(r -> r.getEvent().getTableName().equals(event.getTableName())
		                && r.getEvent().getPrimaryKeyId().equals(event.getPrimaryKeyId()))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		SenderRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(DebeziumException.class.getName(), errorItem.getExceptionType());
		assertEquals(rootCauseMsg, errorItem.getMessage());
		
	}
	
}
