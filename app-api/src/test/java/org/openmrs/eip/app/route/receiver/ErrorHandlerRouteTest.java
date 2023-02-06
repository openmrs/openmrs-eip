package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_FAILED_ENTITIES;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_ERROR_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_PAYLOAD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_SYNC_MESSAGE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_ERROR_HANDLER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_ERROR_HANDLER;
import static org.openmrs.eip.app.route.TestUtils.getEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ERROR_HANDLER + "=DEBUG")
public class ErrorHandlerRouteTest extends BaseReceiverRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return "error-handler-route";
	}
	
	@Before
	public void setup() throws Exception {
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", false);
		
		advise(ROUTE_ID_ERROR_HANDLER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(URI_ERROR_HANDLER);
			}
			
		});
	}
	
	@Test
	public void shouldSkipIfTheApplicationContextIsStopping() {
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", true);
		final int errorCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		
		producerTemplate.send(URI_ERROR_HANDLER, new DefaultExchange(camelContext));
		
		assertMessageLogged(Level.INFO, "Ignoring the error because the application context is stopping");
		assertEquals(errorCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
	}
	
	@Test
	public void shouldAddTheMessageToTheErrorQueue() {
		final String errorMsg = "test error";
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "person-uuid";
		final String payLoad = "{}";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		assertFalse(ReceiverTestUtils.hasRetryItem(modelClass, uuid));
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_PAYLOAD, payLoad);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setSnapshot(true);
		syncMessage.setMessageUuid("message-uuid");
		syncMessage.setOperation(SyncOperation.c);
		syncMessage.setDateCreated(new Date());
		syncMessage.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncMessage.setDateSentBySender(LocalDateTime.now());
		exchange.setProperty(EX_PROP_SYNC_MESSAGE, syncMessage);
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<ReceiverRetryQueueItem> errorItems = TestUtils.getEntities(ReceiverRetryQueueItem.class).stream()
		        .filter(r -> r.getModelClassName().equals(modelClass.getName()) && r.getIdentifier().equals(uuid))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		ReceiverRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(uuid, errorItem.getIdentifier());
		assertEquals(payLoad, errorItem.getEntityPayload());
		assertEquals(EIPException.class.getName(), errorItem.getExceptionType());
		assertEquals(errorMsg, errorItem.getMessage());
		assertEquals(1, errorItem.getAttemptCount().intValue());
		assertNotNull(errorItem.getDateCreated());
		assertEquals(syncMessage.getOperation(), errorItem.getOperation());
		assertEquals(syncMessage.getSite(), errorItem.getSite());
		assertEquals(syncMessage.getDateSentBySender(), errorItem.getDateSentBySender());
		assertEquals(syncMessage.getMessageUuid(), errorItem.getMessageUuid());
		assertEquals(syncMessage.getSnapshot(), errorItem.getSnapshot());
		assertEquals(syncMessage.getDateCreated(), errorItem.getDateReceived());
		assertNull(errorItem.getDateChanged());
		assertTrue(exchange.getProperty(EX_PROP_MOVED_TO_ERROR_QUEUE, Boolean.class));
	}
	
	@Test
	public void shouldSetExceptionTypeToRootCause() {
		final String rootCauseMsg = "test root error";
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "person-uuid";
		final String payLoad = "{}";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException("test1", new Exception("test2", new ActiveMQException(rootCauseMsg))));
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_PAYLOAD, payLoad);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setOperation(SyncOperation.c);
		syncMessage.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncMessage.setDateSentBySender(LocalDateTime.now());
		exchange.setProperty(EX_PROP_SYNC_MESSAGE, syncMessage);
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<ReceiverRetryQueueItem> errorItems = TestUtils.getEntities(ReceiverRetryQueueItem.class).stream()
		        .filter(r -> r.getModelClassName().equals(modelClass.getName()) && r.getIdentifier().equals(uuid))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		ReceiverRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(ActiveMQException.class.getName(), errorItem.getExceptionType());
		assertEquals(rootCauseMsg, errorItem.getMessage());
	}
	
	@Test
	public void shouldProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		ReceiverRetryQueueItem retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retryItem);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
		retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
		Set<String> failedEntities = exchange.getProperty(EX_PROP_FAILED_ENTITIES, Set.class);
		assertEquals(2, failedEntities.size());
		assertTrue(failedEntities.contains(retryItem.getModelClassName() + "#" + retryItem.getIdentifier()));
		assertTrue(failedEntities.contains(PatientModel.class.getName() + "#" + retryItem.getIdentifier()));
	}
	
	@Test
	public void shouldTruncateTheErrorMessageIfItIsLongerThan1024Characters() {
		final String errorMsg = RandomStringUtils.randomAscii(1025);
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "person-uuid";
		final String payLoad = "{}";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		assertFalse(ReceiverTestUtils.hasRetryItem(modelClass, uuid));
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_PAYLOAD, payLoad);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setOperation(SyncOperation.c);
		syncMessage.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncMessage.setDateSentBySender(LocalDateTime.now());
		exchange.setProperty(EX_PROP_SYNC_MESSAGE, syncMessage);
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		List<ReceiverRetryQueueItem> errorItems = TestUtils.getEntities(ReceiverRetryQueueItem.class).stream()
		        .filter(r -> r.getModelClassName().equals(modelClass.getName()) && r.getIdentifier().equals(uuid))
		        .collect(Collectors.toList());
		assertEquals(1, errorItems.size());
		ReceiverRetryQueueItem errorItem = errorItems.get(0);
		assertEquals(EIPException.class.getName(), errorItem.getExceptionType());
		assertEquals(errorMsg.substring(0, 1024), errorItem.getMessage());
	}
	
	@Test
	public void shouldLoadTheRetryItemIfNotSetOnTheExchangeWhenProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		ReceiverRetryQueueItem retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retryItem);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
		retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
	}
	
}
