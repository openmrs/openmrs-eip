package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_CONFLICT_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_ERROR_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_MSG_PROCESSOR;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SiteMessageConsumerIntegrationTest extends BaseReceiverTest {
	
	private static final String MOCK_PROCESSOR_URI = "mock:" + ROUTE_ID_MSG_PROCESSOR;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@EndpointInject(MOCK_PROCESSOR_URI)
	private MockEndpoint mockMsgProcessor;
	
	@Before
	public void setup() {
		mockMsgProcessor.reset();
	}
	
	@Test
	public void fetchNextSyncMessageBatch_shouldGetOnlyNewMessages() throws Exception {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(null, site, 0, null);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		
		List<SyncMessage> syncMessages = consumer.fetchNextSyncMessageBatch();
		
		assertEquals(3, syncMessages.size());
		assertEquals(1l, syncMessages.get(0).getId().longValue());
		assertEquals(2l, syncMessages.get(1).getId().longValue());
		assertEquals(3l, syncMessages.get(2).getId().longValue());
	}
	
	@Test
	public void processMessage_shouldArchiveAndDeleteAProcessedMessage() throws Exception {
		Assert.assertTrue(TestUtils.getEntities(ReceiverSyncArchive.class).isEmpty());
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(MOCK_PROCESSOR_URI, site, 0, null);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		ReceiverActiveMqMessagePublisher mockResponsePublisher = Mockito.mock(ReceiverActiveMqMessagePublisher.class);
		Whitebox.setInternalState(consumer, ReceiverActiveMqMessagePublisher.class, mockResponsePublisher);
		final Long msgId = 2L;
		SyncMessage message = TestUtils.getEntity(SyncMessage.class, msgId);
		mockMsgProcessor.expectedMessageCount(1);
		mockMsgProcessor.whenAnyExchangeReceived(e -> e.setProperty(EX_PROP_MSG_PROCESSED, true));
		
		consumer.processMessage(message);
		
		mockMsgProcessor.assertIsSatisfied();
		Mockito.verify(mockResponsePublisher).sendSyncResponse(message);
		assertNull(TestUtils.getEntity(SyncMessage.class, msgId));
		List<ReceiverSyncArchive> archives = TestUtils.getEntities(ReceiverSyncArchive.class);
		assertEquals(1, archives.size());
		ReceiverSyncArchive archive = archives.get(0);
		assertEquals(message.getMessageUuid(), archive.getMessageUuid());
		assertEquals(message.getModelClassName(), archive.getModelClassName());
		assertEquals(message.getIdentifier(), archive.getIdentifier());
		assertEquals(message.getEntityPayload(), archive.getEntityPayload());
		assertEquals(message.getSite(), archive.getSite());
		assertEquals(message.getSnapshot(), archive.getSnapshot());
		assertEquals(message.getDateSentBySender(), archive.getDateSentBySender());
		assertEquals(message.getDateCreated(), archive.getDateReceived());
		assertNotNull(archive.getDateCreated());
	}
	
	@Test
	public void processMessage_shouldDeleteAMessageMovedToTheConflictQueue() throws Exception {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(MOCK_PROCESSOR_URI, site, 0, null);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		ReceiverActiveMqMessagePublisher mockResponsePublisher = Mockito.mock(ReceiverActiveMqMessagePublisher.class);
		Whitebox.setInternalState(consumer, ReceiverActiveMqMessagePublisher.class, mockResponsePublisher);
		final Long msgId = 2L;
		SyncMessage message = TestUtils.getEntity(SyncMessage.class, msgId);
		mockMsgProcessor.expectedMessageCount(1);
		mockMsgProcessor.whenAnyExchangeReceived(e -> e.setProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, true));
		
		consumer.processMessage(message);
		
		mockMsgProcessor.assertIsSatisfied();
		Mockito.verify(mockResponsePublisher).sendSyncResponse(message);
		assertNull(TestUtils.getEntity(SyncMessage.class, msgId));
	}
	
	@Test
	public void processMessage_shouldDeleteAMessageMovedToTheErrorQueue() throws Exception {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(MOCK_PROCESSOR_URI, site, 0, null);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		ReceiverActiveMqMessagePublisher mockResponsePublisher = Mockito.mock(ReceiverActiveMqMessagePublisher.class);
		Whitebox.setInternalState(consumer, ReceiverActiveMqMessagePublisher.class, mockResponsePublisher);
		final Long msgId = 2L;
		SyncMessage message = TestUtils.getEntity(SyncMessage.class, msgId);
		mockMsgProcessor.expectedMessageCount(1);
		mockMsgProcessor.whenAnyExchangeReceived(e -> e.setProperty(EX_PROP_MOVED_TO_ERROR_QUEUE, true));
		
		consumer.processMessage(message);
		
		mockMsgProcessor.assertIsSatisfied();
		Mockito.verify(mockResponsePublisher).sendSyncResponse(message);
		assertNull(TestUtils.getEntity(SyncMessage.class, msgId));
	}
	
	@Test
	public void processMessage_shouldFailIfSyncOutComeIsUnknown() throws Exception {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(MOCK_PROCESSOR_URI, site, 0, null);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		ReceiverActiveMqMessagePublisher mockResponsePublisher = Mockito.mock(ReceiverActiveMqMessagePublisher.class);
		Whitebox.setInternalState(consumer, ReceiverActiveMqMessagePublisher.class, mockResponsePublisher);
		final Long msgId = 2L;
		SyncMessage message = TestUtils.getEntity(SyncMessage.class, msgId);
		mockMsgProcessor.expectedMessageCount(1);
		Exception thrown = Assert.assertThrows(EIPException.class, () -> consumer.processMessage(message));
		assertEquals("Something went wrong while processing sync message with id: " + msgId, thrown.getMessage());
		
		mockMsgProcessor.assertIsSatisfied();
		Mockito.verify(mockResponsePublisher).sendSyncResponse(message);
		Assert.assertNotNull(TestUtils.getEntity(SyncMessage.class, msgId));
	}
	
}
