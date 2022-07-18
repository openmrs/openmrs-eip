package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_DBZM_EVENT_READER;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_READER;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBZM_EVENT_READER + "=DEBUG")
public class DebeziumEventReaderTest extends BaseSenderRouteTest {
	
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
	public void shouldLoadDebeziumEventsSortedByDateCreatedAndCallTheEventProcessor() throws Exception {
		List<DebeziumEvent> events = repo.findAll();
		assertEquals(2, events.size());
		Assert.assertTrue(events.get(0).getDateCreated().getTime() > (events.get(1).getDateCreated().getTime()));
		mockEventProcessor.expectedBodyReceived().body(List.class).isEqualTo(events);
		DefaultExchange exchange = new DefaultExchange(camelContext);
		
		producerTemplate.send(URI_DBZM_EVENT_READER, exchange);
		
		mockEventProcessor.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Read " + events.size() + " item(s) from the debezium event queue");
		assertEquals(2, exchange.getIn().getBody(List.class).size());
		assertEquals(2, ((DebeziumEvent) exchange.getIn().getBody(List.class).get(0)).getId().intValue());
		assertEquals(1, ((DebeziumEvent) exchange.getIn().getBody(List.class).get(1)).getId().intValue());
	}
	
}
