package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.management.entity.SenderSyncMessage.SenderSyncMessageStatus.NEW;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_SYNC_MSG_READER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_SYNC_MSG_READER;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_SYNC_MSG_READER + "=DEBUG")
public class SenderSyncMsgReaderRouteTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_SYNC_MSG_READER)
	private MockEndpoint mockProcessor;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_SYNC_MSG_READER;
	}
	
	@Before
	public void setup() throws Exception {
		mockProcessor.reset();
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", false);
		
		advise(ROUTE_ID_SYNC_MSG_READER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockProcessor);
			}
			
		});
	}
	
	@Test
	public void shouldNotCallTheSenderSyncMessageProcessorIfNoEventsExists() throws Exception {
		mockProcessor.expectedMessageCount(0);
		
		producerTemplate.send(URI_SYNC_MSG_READER, new DefaultExchange(camelContext));
		
		mockProcessor.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "No sync messages found");
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
	public void shouldLoadSenderSyncMessagesSortedByDateCreatedAndCallTheMsgProcessor() throws Exception {
		final int msgCount = 3;
		List<SenderSyncMessage> msgs = TestUtils.getEntities(SenderSyncMessage.class).stream()
		        .filter(m -> m.getStatus() == NEW).collect(Collectors.toList());
		assertEquals(msgCount, msgs.size());
		assertTrue(msgs.get(0).getDateCreated().getTime() > (msgs.get(2).getDateCreated().getTime()));
		assertTrue(msgs.get(1).getDateCreated().getTime() > (msgs.get(2).getDateCreated().getTime()));
		assertTrue(msgs.get(0).getDateSent().getTime() < (msgs.get(2).getDateSent().getTime()));
		assertTrue(msgs.get(0).getEventDate().getTime() < (msgs.get(2).getEventDate().getTime()));
		mockProcessor.expectedBodyReceived().body(List.class).isEqualTo(msgs);
		List<SenderSyncMessage> processedMsgs = new ArrayList();
		mockProcessor.whenAnyExchangeReceived(e -> {
			processedMsgs.addAll(e.getIn().getBody(List.class));
			processedMsgs.forEach(m -> {
				m.markAsSent();
				TestUtils.updateEntity(m);
			});
		});
		
		producerTemplate.send(URI_SYNC_MSG_READER, new DefaultExchange(camelContext));
		
		mockProcessor.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Fetched " + msgs.size() + " sender sync message(s)");
		assertEquals(msgCount, processedMsgs.size());
		assertEquals(3, processedMsgs.get(0).getId().intValue());
		assertEquals(1, processedMsgs.get(1).getId().intValue());
		assertEquals(2, processedMsgs.get(2).getId().intValue());
		assertEquals(0, TestUtils.getEntities(SenderSyncMessage.class).stream().filter(m -> m.getStatus() == NEW).count());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
	public void shouldNotReadAnyMessagesIfTheApplicationIsStopping() throws Exception {
		AppUtils.setAppContextStopping();
		mockProcessor.expectedMessageCount(0);
		
		producerTemplate.send(URI_SYNC_MSG_READER, new DefaultExchange(camelContext));
		
		mockProcessor.assertIsSatisfied();
	}
	
}
