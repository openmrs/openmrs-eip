package org.openmrs.eip.app.route.receiver;

import static org.openmrs.eip.app.SyncConstants.EX_APP_ID;
import static org.openmrs.eip.app.SyncConstants.ROUTE_ID_SHUTDOWN;
import static org.openmrs.eip.app.SyncConstants.URI_SHUTDOWN;
import static org.openmrs.eip.app.route.receiver.ShutdownRouteTest.TEST_RECEIVER_ID;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ScriptDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = ReceiverConstants.PROP_RECEIVER_ID + "=" + TEST_RECEIVER_ID)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_SHUTDOWN + "=DEBUG")
public class ShutdownRouteTest extends BaseReceiverRouteTest {
	
	protected static final String TEST_RECEIVER_ID = "test";
	
	@EndpointInject("mock:email")
	private MockEndpoint mockEmailNoticeProcessor;
	
	@EndpointInject("mock:shutdown")
	private MockEndpoint mockShutdownBean;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_SHUTDOWN;
	}
	
	@Before
	public void setup() throws Exception {
		setInternalState(AppUtils.class, "appContextStopping", false);
		setInternalState(AppUtils.class, "shuttingDown", false);
		mockEmailNoticeProcessor.reset();
		mockShutdownBean.reset();
		
		advise(ROUTE_ID_SHUTDOWN, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockEmailNoticeProcessor);
				weaveByType(ScriptDefinition.class).selectLast().replace().to(mockShutdownBean);
			}
			
		});
	}
	
	@Test
	public void shouldSkipIfTheApplicationContextIsStopping() throws Exception {
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", true);
		mockEmailNoticeProcessor.expectedMessageCount(0);
		mockShutdownBean.expectedMessageCount(0);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		mockEmailNoticeProcessor.assertIsSatisfied();
		mockShutdownBean.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "The application context is already stopping");
	}
	
	@Test
	public void shouldSkipProcessingIfTheReceiverIsAlreadyShuttingDown() throws Exception {
		Whitebox.setInternalState(AppUtils.class, "shuttingDown", true);
		mockEmailNoticeProcessor.expectedMessageCount(0);
		mockShutdownBean.expectedMessageCount(0);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		mockEmailNoticeProcessor.assertIsSatisfied();
		mockShutdownBean.assertIsSatisfied();
		assertMessageLogged(Level.DEBUG, "The application is already shutting down");
	}
	
	@Test
	public void shouldCallEmailProcessorAndShutdownTheApplication() throws Exception {
		mockEmailNoticeProcessor.expectedMessageCount(1);
		mockShutdownBean.expectedMessageCount(1);
		mockEmailNoticeProcessor.expectedPropertyReceived(EX_APP_ID, TEST_RECEIVER_ID);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		mockEmailNoticeProcessor.assertIsSatisfied();
		mockShutdownBean.assertIsSatisfied();
	}
	
}
