package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.route.TestUtils.getEntities;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_RESPONSE_READER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_RESPONSE_READER;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.openmrs.eip.app.route.TestUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_RESPONSE_READER + "=DEBUG")
public class SyncResponseReaderRouteTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_RESPONSE_READER)
	private MockEndpoint mockProcessor;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_RESPONSE_READER;
	}
	
	@Before
	public void setup() throws Exception {
		mockProcessor.reset();
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", false);
		advise(ROUTE_ID_RESPONSE_READER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockProcessor);
			}
			
		});
	}
	
	@Test
	public void shouldDoNothingIfNoSyncResponsesAreFound() throws Exception {
		mockProcessor.expectedMessageCount(0);
		
		producerTemplate.send(URI_RESPONSE_READER, new DefaultExchange(camelContext));
		
		mockProcessor.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "No sync responses was found");
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = "classpath:mgt_sender_sync_response.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldLoadAndProcessAllNewSyncResponsesSortedByDateCreated() throws Exception {
		final int messageCount = 4;
		assertFalse(getEntities(SenderSyncMessage.class).isEmpty());
		List<SenderSyncResponse> responses = getEntities(SenderSyncResponse.class);
		assertEquals(messageCount, responses.size());
		assertTrue(responses.get(0).getDateCreated().getTime() > (responses.get(2).getDateCreated().getTime()));
		assertTrue(responses.get(1).getDateCreated().getTime() > (responses.get(2).getDateCreated().getTime()));
		List<SenderSyncResponse> processedResponses = new ArrayList();
		mockProcessor.whenAnyExchangeReceived(e -> {
			processedResponses.addAll(e.getIn().getBody(List.class));
			TestUtils.deleteAll(SenderSyncMessage.class);
			TestUtils.deleteAll(SenderSyncResponse.class);
		});
		
		producerTemplate.send(URI_RESPONSE_READER, new DefaultExchange(camelContext));
		
		assertMessageLogged(Level.INFO, "Fetched " + responses.size() + " sender sync response(s)");
		assertEquals(messageCount, processedResponses.size());
		assertEquals(3, processedResponses.get(0).getId().intValue());
		assertEquals(4, processedResponses.get(1).getId().intValue());
		assertEquals(1, processedResponses.get(2).getId().intValue());
		assertEquals(2, processedResponses.get(3).getId().intValue());
		assertTrue(getEntities(SenderSyncResponse.class).isEmpty());
		assertTrue(getEntities(SenderSyncMessage.class).isEmpty());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_response.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldNotReadAnyResponsesIfTheApplicationIsStopping() throws Exception {
		AppUtils.setAppContextStopping();
		mockProcessor.expectedMessageCount(0);
		
		producerTemplate.send(URI_RESPONSE_READER, new DefaultExchange(camelContext));
		
		mockProcessor.assertIsSatisfied();
	}
	
}
