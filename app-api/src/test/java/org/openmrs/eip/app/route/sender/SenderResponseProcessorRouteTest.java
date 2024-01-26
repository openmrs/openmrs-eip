package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.route.TestUtils.getEntities;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_RESPONSE_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_RESPONSE_PROCESSOR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.entity.sender.SenderSyncResponse;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.app.management.repository.SenderSyncResponseRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_RESPONSE_PROCESSOR + "=DEBUG")
public class SenderResponseProcessorRouteTest extends BaseSenderRouteTest {
	
	private static final String TEST_LISTENER = "mock:listener";
	
	@EndpointInject(TEST_LISTENER)
	private MockEndpoint mockListener;
	
	@Autowired
	private SenderSyncArchiveRepository archiveRepo;
	
	@Autowired
	private SenderSyncMessageRepository syncRepo;
	
	@Autowired
	private SenderSyncResponseRepository responseRepo;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_RESPONSE_PROCESSOR;
	}
	
	private SenderSyncResponse createSyncResponse(String messageUuid) {
		SenderSyncResponse response = new SenderSyncResponse();
		response.setMessageUuid(messageUuid);
		response.setDateSentByReceiver(LocalDateTime.now());
		response.setDateReceivedByReceiver(LocalDateTime.now());
		response.setDateCreated(new Date());
		TestUtils.saveEntity(response);
		return response;
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = "classpath:mgt_sender_sync_response.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldProcessAResponseForASyncMessageWithAMessageUuidMatches() {
		assertEquals(0, archiveRepo.count());
		final String msgUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		SenderSyncMessage msg = syncRepo.getReferenceById(4L);
		assertEquals(msgUuid, msg.getMessageUuid());
		SenderSyncResponse response = responseRepo.getReferenceById(4L);
		assertEquals(msgUuid, response.getMessageUuid());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(response);
		
		producerTemplate.send(URI_RESPONSE_PROCESSOR, exchange);
		
		assertFalse(syncRepo.findById(msg.getId()).isPresent());
		assertFalse(responseRepo.findById(response.getId()).isPresent());
		List<SenderSyncArchive> archives = archiveRepo.findAll();
		assertEquals(1, archives.size());
		SenderSyncArchive archive = archives.get(0);
		assertEquals(msg.getIdentifier(), archive.getIdentifier());
		assertEquals(msg.getTableName(), archive.getTableName());
		assertEquals(msg.getOperation(), archive.getOperation());
		assertEquals(msg.getEventDate(), archive.getEventDate());
		assertEquals(msg.getSnapshot(), archive.getSnapshot());
		assertEquals(msg.getMessageUuid(), archive.getMessageUuid());
		assertEquals(msg.getRequestUuid(), archive.getRequestUuid());
		assertEquals(msg.getDateSent(), archive.getDateSent());
		assertEquals(response.getDateReceivedByReceiver(), archive.getDateReceivedByReceiver());
		assertNotNull(msg.getDateCreated());
	}
	
	@Test
	public void shouldProcessAResponseAndTheSyncMessageIsNotFound() {
		assertTrue(TestUtils.getEntities(SenderSyncArchive.class).isEmpty());
		final String msgUuid = "msg-uuid";
		assertTrue(getEntities(SenderSyncResponse.class).isEmpty());
		SenderSyncResponse response = createSyncResponse(msgUuid);
		assertEquals(1, getEntities(SenderSyncResponse.class).size());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(response);
		
		producerTemplate.send(URI_RESPONSE_PROCESSOR, exchange);
		
		assertMessageLogged(Level.INFO, "No sync message was found with message uuid " + msgUuid);
		assertTrue(getEntities(SenderSyncResponse.class).isEmpty());
		assertTrue(TestUtils.getEntities(SenderSyncArchive.class).isEmpty());
	}
	
}
