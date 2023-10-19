package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.ROUTE_ID_SHUTDOWN;
import static org.openmrs.eip.app.SyncConstants.URI_SHUTDOWN;
import static org.openmrs.eip.app.route.sender.ShutdownRouteTest.TEST_SENDER_ID;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ScriptDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.sender.SenderConstants;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = SenderConstants.PROP_SENDER_ID + "=" + TEST_SENDER_ID)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_SHUTDOWN + "=DEBUG")
public class ShutdownRouteTest extends BaseSenderRouteTest {
	
	protected static final String TEST_SENDER_ID = "test";
	
	@EndpointInject("mock:shutdown")
	private MockEndpoint mockShutdownBean;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_SHUTDOWN;
	}
	
	@Before
	public void setup() throws Exception {
		setInternalState(CustomFileOffsetBackingStore.class, "disabled", false);
		setInternalState(AppUtils.class, "shuttingDown", false);
		mockShutdownBean.reset();
		
		advise(ROUTE_ID_SHUTDOWN, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ScriptDefinition.class).selectLast().replace().to(mockShutdownBean);
			}
			
		});
	}
	
	@Test
	public void shouldDisableTheDebeziumBackingStoreCallEmailProcessorAndShutdownTheApplication() throws Exception {
		assertFalse(CustomFileOffsetBackingStore.isDisabled());
		mockShutdownBean.expectedMessageCount(1);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		assertTrue(CustomFileOffsetBackingStore.isDisabled());
		mockShutdownBean.assertIsSatisfied();
	}
	
	@Test
	public void shouldDisableTheDebeziumBackingStoreAndSkipCallingEmailProcessorAndShutdown() throws Exception {
		setInternalState(AppUtils.class, "shuttingDown", true);
		assertFalse(CustomFileOffsetBackingStore.isDisabled());
		mockShutdownBean.expectedMessageCount(0);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		assertTrue(CustomFileOffsetBackingStore.isDisabled());
		mockShutdownBean.assertIsSatisfied();
	}
	
	@Test
	public void shouldSkipIfTheApplicationIsStopping() throws Exception {
		Whitebox.setInternalState(AppUtils.class, "shuttingDown", true);
		mockShutdownBean.expectedMessageCount(0);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		mockShutdownBean.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "The application is already shutting down");
	}
	
}
