package org.openmrs.eip.mysql.watcher.route;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DBZM_MSG_PROCESSOR;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.ERROR_HANDLER_REF;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.EX_PROP_SKIP;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.ID_SETTING_PROCESSOR;
import static org.openmrs.eip.mysql.watcher.route.DebeziumRoute.ROUTE_ID_EVENT_LISTENER;
import static org.openmrs.eip.mysql.watcher.route.DebeziumRoute.URI_EVENT_LISTENER;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.mysql.watcher.CustomFileOffsetBackingStore;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=false")
public class DebeziumRouteTest extends BaseWatcherRouteTest {
	
	@EndpointInject("mock:" + DBZM_MSG_PROCESSOR)
	private MockEndpoint mockDbzmEndpoint;
	
	@EndpointInject("mock:" + ID_SETTING_PROCESSOR)
	private MockEndpoint mockIdSettingEndpoint;
	
	@EndpointInject("mock:" + ROUTE_ID_EVENT_LISTENER)
	private MockEndpoint mockEventListenerEndpoint;
	
	protected static final String URI = "direct:" + DEBEZIUM_ROUTE_ID;
	
	@BeforeEach
	public void setup() throws Exception {
		setInternalState(CustomFileOffsetBackingStore.class, "disabled", false);
		mockDbzmEndpoint.reset();
		mockIdSettingEndpoint.reset();
		mockEventListenerEndpoint.reset();
		
		camelContext.addRoutes(new DebeziumRoute(URI, ERROR_HANDLER_REF));
		
		advise(DEBEZIUM_ROUTE_ID, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).selectFirst().replace().to(mockDbzmEndpoint);
				weaveByType(ProcessDefinition.class).selectLast().replace().to(mockIdSettingEndpoint);
				interceptSendToEndpoint(URI_EVENT_LISTENER).skipSendToOriginalEndpoint().to(mockEventListenerEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldProcessTheEvent() throws Exception {
		mockDbzmEndpoint.expectedMessageCount(1);
		mockIdSettingEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedMessageCount(1);
		
		producerTemplate.sendBody(URI, null);
		
		mockDbzmEndpoint.assertIsSatisfied();
		mockIdSettingEndpoint.assertIsSatisfied();
		mockEventListenerEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldNotProcessTheEventIfTheOffsetBackingStoreStoreIsDisabled() throws Exception {
		CustomFileOffsetBackingStore.disable();
		mockDbzmEndpoint.expectedMessageCount(0);
		mockIdSettingEndpoint.expectedMessageCount(0);
		mockEventListenerEndpoint.expectedMessageCount(0);
		
		producerTemplate.sendBody(URI, null);
		
		mockDbzmEndpoint.assertIsSatisfied();
		mockIdSettingEndpoint.assertIsSatisfied();
		mockEventListenerEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG,
		    "Deferring DB event because an error was encountered while processing a previous one");
	}
	
	@Test
	public void shouldProcessTheEventIfSkipIsSetToNull() throws Exception {
		mockDbzmEndpoint.expectedMessageCount(1);
		mockIdSettingEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedMessageCount(1);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_SKIP, null);
		
		producerTemplate.sendBody(URI, null);
		
		mockDbzmEndpoint.assertIsSatisfied();
		mockIdSettingEndpoint.assertIsSatisfied();
		mockEventListenerEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldProcessTheEventIfSkipIsSetToFalse() throws Exception {
		mockDbzmEndpoint.expectedMessageCount(1);
		mockIdSettingEndpoint.expectedMessageCount(1);
		mockEventListenerEndpoint.expectedMessageCount(1);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_SKIP, false);
		
		producerTemplate.sendBody(URI, null);
		
		mockDbzmEndpoint.assertIsSatisfied();
		mockIdSettingEndpoint.assertIsSatisfied();
		mockEventListenerEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldNotProcessTheEventIfMarkedForSkipping() throws Exception {
		mockDbzmEndpoint.expectedMessageCount(1);
		mockIdSettingEndpoint.expectedMessageCount(0);
		mockEventListenerEndpoint.expectedMessageCount(0);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_SKIP, true);
		
		producerTemplate.send(URI, exchange);
		
		mockDbzmEndpoint.assertIsSatisfied();
		mockIdSettingEndpoint.assertIsSatisfied();
		mockEventListenerEndpoint.assertIsSatisfied();
		assertNull(getException(exchange));
	}
}
