package org.openmrs.eip.app.route.sender;

import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBZM_EVENT_READER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_READER;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBZM_EVENT_READER + "=DEBUG")
public class debeziumEventReaderTest extends BaseSenderRouteTest {
	
	@EndpointInject("mock:" + ROUTE_ID_DBZM_EVENT_READER)
	private MockEndpoint mockEventProcessor;
	
	@Autowired
	private DebeziumEventRepository repo;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_DBZM_EVENT_READER;
	}
	
	@Before
	public void setup() throws Exception {
		mockEventProcessor.reset();
		advise(ROUTE_ID_DBZM_EVENT_READER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockEventProcessor);
			}
			
		});
	}
	
	@Test
	public void shouldNotCallTheDebeziumEventProcessorIfNoEventsExists() throws Exception {
		mockEventProcessor.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBZM_EVENT_READER, new DefaultExchange(camelContext));
		
		mockEventProcessor.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "No events found in the debezium event queue");
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
	public void shouldCallTheDebeziumEventProcessor() throws Exception {
		mockEventProcessor.expectedBodyReceived().body(List.class).isEqualTo(repo.findAll());
		
		producerTemplate.send(URI_DBZM_EVENT_READER, new DefaultExchange(camelContext));
		
		mockEventProcessor.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Read " + repo.findAll().size() + " item(s) from the debezium event queue");
	}
	
}
