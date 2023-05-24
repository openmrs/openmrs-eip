package org.openmrs.eip.app.route.receiver;

import static java.util.Collections.synchronizedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.TestConstants.URI_ERROR_HANDLER;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_FAILED_ENTITIES;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_CONFLICT_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_MSG_DESTINATION;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_RETRY;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_RETRY;
import static org.openmrs.eip.app.route.TestUtils.getEntities;
import static org.openmrs.eip.app.route.receiver.ReceiverRetryRouteTest.ROUTE_ID_DESTINATION;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.BeanDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = PROP_MSG_DESTINATION + "=" + ROUTE_ID_DESTINATION)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_RETRY + "=DEBUG")
public class ReceiverRetryRouteTest extends BaseReceiverRouteTest {
	
	protected static final String ROUTE_ID_DESTINATION = "msg-processor";
	
	@EndpointInject("mock:" + ROUTE_ID_DESTINATION)
	private MockEndpoint mockMsgProcessorEndpoint;
	
	private TestBean testCacheBean;
	
	private TestBean testIndexBean;
	
	public class TestBean {
		
		List<ReceiverRetryQueueItem> retries = new ArrayList();
		
		public void process(ReceiverRetryQueueItem retry) {
			retries.add(retry);
		}
	}
	
	@Override
	public String getTestRouteFilename() {
		return "retry-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockMsgProcessorEndpoint.reset();
		testCacheBean = new TestBean();
		testIndexBean = new TestBean();
		
		advise(ROUTE_ID_RETRY, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint("direct:" + ROUTE_ID_DESTINATION).skipSendToOriginalEndpoint()
				        .to(mockMsgProcessorEndpoint);
				weaveByType(BeanDefinition.class).selectFirst().replace().bean(testCacheBean,
				    "process(${exchangeProperty.retry-item})");
				weaveByType(BeanDefinition.class).selectLast().replace().bean(testIndexBean,
				    "process(${exchangeProperty.retry-item})");
			}
			
		});
	}
	
	@Test
	public void shouldNotCallTheMsgDestinationIfNoRetriesExists() throws Exception {
		mockMsgProcessorEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_RETRY, new DefaultExchange(camelContext));
		
		assertTrue(testCacheBean.retries.isEmpty());
		assertTrue(testIndexBean.retries.isEmpty());
		assertMessageLogged(Level.DEBUG, "No messages found in the retry queue");
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldLoadAllRetryItemsSortedByDateReceivedAndCallTheEventProcessorForEach() throws Exception {
		final int retryCount = 5;
		List<ReceiverRetryQueueItem> retries = TestUtils.getEntities(ReceiverRetryQueueItem.class);
		for (ReceiverRetryQueueItem retry : retries) {
			assertEquals(1, retry.getAttemptCount().intValue());
		}
		assertEquals(retryCount, retries.size());
		DefaultExchange exchange = new DefaultExchange(camelContext);
		mockMsgProcessorEndpoint.expectedMessageCount(retryCount);
		List<SyncModel> receivedBodies = new ArrayList();
		List<Long> receivedRetryItemIds = new ArrayList();
		List<ReceiverRetryQueueItem> receivedRetryItems = new ArrayList();
		mockMsgProcessorEndpoint.whenAnyExchangeReceived(e -> {
			receivedBodies.add(e.getIn().getBody(SyncModel.class));
			receivedRetryItemIds.add(e.getProperty(EX_PROP_RETRY_ITEM_ID, Long.class));
			receivedRetryItems.add(e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class));
			e.setProperty(EX_PROP_MSG_PROCESSED, true);
		});
		
		producerTemplate.send(URI_RETRY, exchange);
		
		mockMsgProcessorEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Message count in the retry queue: " + retries.size());
		assertEquals(retryCount, receivedBodies.size());
		assertEquals(retryCount, receivedRetryItemIds.size());
		assertEquals(retryCount, receivedRetryItems.size());
		assertEquals(4L, receivedRetryItemIds.get(0).longValue());
		assertEquals(1L, receivedRetryItemIds.get(1).longValue());
		assertEquals(2L, receivedRetryItemIds.get(2).longValue());
		assertEquals(3L, receivedRetryItemIds.get(3).longValue());
		assertEquals(5L, receivedRetryItemIds.get(4).longValue());
		for (ReceiverRetryQueueItem retry : receivedRetryItems) {
			assertEquals(2, retry.getAttemptCount().intValue());
		}
		Assert.assertTrue(TestUtils.getEntities(ReceiverRetryQueueItem.class).isEmpty());
		Assert.assertTrue(exchange.getProperty(EX_PROP_FAILED_ENTITIES, List.class).isEmpty());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldFailIfAnEntityAlreadyHasAFailedRetryItemInTheCurrentIteration() throws Exception {
		List<ReceiverRetryQueueItem> retries = TestUtils.getEntities(ReceiverRetryQueueItem.class);
		assertEquals(5, retries.size());
		List<Exchange> failedExchanges = new ArrayList();
		advise(ROUTE_ID_RETRY, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(URI_ERROR_HANDLER).process(e -> failedExchanges.add(e));
			}
			
		});
		
		DefaultExchange exchange = new DefaultExchange(camelContext);
		mockMsgProcessorEndpoint.expectedMessageCount(3);
		mockMsgProcessorEndpoint.whenAnyExchangeReceived(e -> {
			if (e.getProperty(EX_PROP_RETRY_ITEM_ID, Long.class) == 1L) {
				ReceiverRetryQueueItem item = e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class);
				exchange.getProperty(EX_PROP_FAILED_ENTITIES, Set.class)
				        .addAll(Arrays.asList(item.getModelClassName() + "#" + item.getIdentifier(),
				            PatientModel.class.getName() + "#" + item.getIdentifier()));
				throw new EIPException("Some error");
			} else {
				e.setProperty(EX_PROP_MSG_PROCESSED, true);
			}
		});
		
		producerTemplate.send(URI_RETRY, exchange);
		
		mockMsgProcessorEndpoint.assertIsSatisfied();
		assertEquals(2, testCacheBean.retries.size());
		assertEquals(2, testIndexBean.retries.size());
		assertEquals(2, failedExchanges.size());
		for (Exchange e : failedExchanges) {
			assertEquals("Skipped because the entity had older failed message(s) in the queue", getErrorMessage(e));
			assertEquals(2, e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class).getAttemptCount().intValue());
			assertEquals(2, e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class).getAttemptCount().intValue());
		}
		retries = TestUtils.getEntities(ReceiverRetryQueueItem.class);
		assertEquals(3, retries.size());
		assertEquals(1, retries.get(0).getId().longValue());
		assertEquals(2, retries.get(1).getId().longValue());
		assertEquals(3, retries.get(2).getId().longValue());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldProcessARetryItemAndMoveItFromTheErrorToTheArchivesQueue() throws Exception {
		final String uuid = "person-uuid";
		assertTrue(getEntities(ReceiverRetryQueueItem.class).isEmpty());
		assertTrue(getEntities(ReceiverSyncArchive.class).isEmpty());
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setMessageUuid("message-uuid");
		retry.setModelClassName(PersonModel.class.getName());
		retry.setIdentifier(uuid);
		retry.setOperation(SyncOperation.c);
		retry.setSnapshot(true);
		retry.setDateCreated(new Date());
		retry.setAttemptCount(1);
		retry.setEntityPayload("{}");
		retry.setExceptionType(EIPException.class.getName());
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setDateReceived(new Date());
		retry.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		TestUtils.saveEntity(retry);
		assertEquals(1, getEntities(ReceiverRetryQueueItem.class).size());
		mockMsgProcessorEndpoint.expectedMessageCount(1);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_RETRY_ITEM_ID, retry.getId());
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_RETRY_ITEM, retry);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_MODEL_CLASS, PersonModel.class.getName());
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_ENTITY_ID, uuid);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_FAILED_ENTITIES, synchronizedSet(new HashSet()));
		final AtomicInteger attemptCountHolder = new AtomicInteger();
		mockMsgProcessorEndpoint.whenAnyExchangeReceived(e -> {
			attemptCountHolder.set(e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class).getAttemptCount());
			e.setProperty(EX_PROP_MSG_PROCESSED, true);
		});
		
		producerTemplate.send(URI_RETRY, new DefaultExchange(camelContext));
		
		mockMsgProcessorEndpoint.assertIsSatisfied();
		assertEquals(1, testCacheBean.retries.size());
		assertTrue(testCacheBean.retries.contains(retry));
		assertEquals(1, testIndexBean.retries.size());
		assertTrue(testIndexBean.retries.contains(retry));
		assertTrue(getEntities(ReceiverRetryQueueItem.class).isEmpty());
		assertEquals(2, attemptCountHolder.get());
		List<ReceiverSyncArchive> archives = TestUtils.getEntities(ReceiverSyncArchive.class);
		assertEquals(1, archives.size());
		ReceiverSyncArchive archive = archives.get(0);
		assertEquals(retry.getMessageUuid(), archive.getMessageUuid());
		assertEquals(retry.getModelClassName(), archive.getModelClassName());
		assertEquals(retry.getIdentifier(), archive.getIdentifier());
		assertEquals(retry.getEntityPayload(), archive.getEntityPayload());
		assertEquals(retry.getSite(), archive.getSite());
		assertEquals(retry.getSnapshot(), archive.getSnapshot());
		assertEquals(retry.getDateSentBySender(), archive.getDateSentBySender());
		assertEquals(retry.getDateReceived(), archive.getDateReceived());
		assertNotNull(archive.getDateCreated());
	}
	
	@Test
	public void shouldProcessARetryItemAndRemoveItFromTheErrorQueueIfAConflictIsEncountered() throws Exception {
		final String uuid = "person-uuid";
		assertTrue(getEntities(ReceiverRetryQueueItem.class).isEmpty());
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(PersonModel.class.getName());
		retry.setIdentifier(uuid);
		retry.setOperation(SyncOperation.c);
		retry.setDateCreated(new Date());
		retry.setAttemptCount(1);
		retry.setEntityPayload("{}");
		retry.setExceptionType(EIPException.class.getName());
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setDateReceived(new Date());
		TestUtils.saveEntity(retry);
		assertEquals(1, getEntities(ReceiverRetryQueueItem.class).size());
		mockMsgProcessorEndpoint.expectedMessageCount(1);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_RETRY_ITEM_ID, retry.getId());
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_RETRY_ITEM, retry);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_MODEL_CLASS, PersonModel.class.getName());
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_ENTITY_ID, uuid);
		mockMsgProcessorEndpoint.expectedPropertyReceived(EX_PROP_FAILED_ENTITIES, synchronizedSet(new HashSet()));
		final AtomicInteger attemptCountHolder = new AtomicInteger();
		mockMsgProcessorEndpoint.whenAnyExchangeReceived(e -> {
			attemptCountHolder.set(e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class).getAttemptCount());
			e.setProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, true);
		});
		
		producerTemplate.send(URI_RETRY, new DefaultExchange(camelContext));
		
		mockMsgProcessorEndpoint.assertIsSatisfied();
		assertTrue(testCacheBean.retries.isEmpty());
		assertTrue(testIndexBean.retries.isEmpty());
		assertTrue(getEntities(ReceiverRetryQueueItem.class).isEmpty());
		assertEquals(2, attemptCountHolder.get());
	}
	
	@Test
	public void shouldFailForAnUnknownOutComeWhenProcessingARetryItem() throws Exception {
		final String uuid = "person-uuid";
		assertTrue(getEntities(ReceiverRetryQueueItem.class).isEmpty());
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(PersonModel.class.getName());
		retry.setIdentifier(uuid);
		retry.setOperation(SyncOperation.c);
		retry.setDateCreated(new Date());
		retry.setAttemptCount(1);
		retry.setEntityPayload("{}");
		retry.setExceptionType(EIPException.class.getName());
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setDateReceived(new Date());
		TestUtils.saveEntity(retry);
		assertEquals(1, getEntities(ReceiverRetryQueueItem.class).size());
		final AtomicInteger attemptCountHolder = new AtomicInteger();
		mockMsgProcessorEndpoint.whenAnyExchangeReceived(
		    e -> attemptCountHolder.set(e.getProperty(EX_PROP_RETRY_ITEM, ReceiverRetryQueueItem.class).getAttemptCount()));
		List<Exchange> failedExchanges = new ArrayList();
		advise(ROUTE_ID_RETRY, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(URI_ERROR_HANDLER).process(e -> failedExchanges.add(e));
			}
			
		});
		
		producerTemplate.send(URI_RETRY, new DefaultExchange(camelContext));
		
		assertTrue(testCacheBean.retries.isEmpty());
		assertTrue(testIndexBean.retries.isEmpty());
		assertNotNull(TestUtils.getEntity(ReceiverRetryQueueItem.class, retry.getId()));
		assertEquals(2, attemptCountHolder.get());
		assertEquals(1, failedExchanges.size());
		assertEquals("Something went wrong while processing sync message with id: " + retry.getId(),
		    getErrorMessage(failedExchanges.get(0)));
	}
	
}
