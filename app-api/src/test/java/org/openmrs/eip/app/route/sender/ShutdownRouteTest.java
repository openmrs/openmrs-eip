package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.route.sender.ShutdownRouteTest.TEST_SENDER_ID;
import static org.openmrs.eip.app.sender.SenderConstants.EX_APP_ID;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_SHUTDOWN;
import static org.openmrs.eip.app.sender.SenderConstants.URI_SHUTDOWN;
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
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.sender.SenderConstants;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = SenderConstants.PROP_SENDER_ID + "=" + TEST_SENDER_ID)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_SHUTDOWN + "=DEBUG")
public class ShutdownRouteTest extends BaseSenderRouteTest {
	
	protected static final String TEST_SENDER_ID = "test";
	
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
		setInternalState(CustomFileOffsetBackingStore.class, "disabled", false);
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
	public void shouldDisableTheDebeziumBackingStoreCallEmailProcessorAndShutdownTheApplication() throws Exception {
		assertFalse(CustomFileOffsetBackingStore.isDisabled());
		mockEmailNoticeProcessor.expectedMessageCount(1);
		mockShutdownBean.expectedMessageCount(1);
		mockEmailNoticeProcessor.expectedPropertyReceived(EX_APP_ID, TEST_SENDER_ID);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		assertTrue(CustomFileOffsetBackingStore.isDisabled());
		mockEmailNoticeProcessor.assertIsSatisfied();
		mockShutdownBean.assertIsSatisfied();
	}
	
	@Test
	public void shouldDisableTheDebeziumBackingStoreAndSkipCallingEmailProcessorAndShutdown() throws Exception {
		setInternalState(AppUtils.class, "shuttingDown", true);
		assertFalse(CustomFileOffsetBackingStore.isDisabled());
		mockEmailNoticeProcessor.expectedMessageCount(0);
		mockShutdownBean.expectedMessageCount(0);
		
		producerTemplate.send(URI_SHUTDOWN, new DefaultExchange(camelContext));
		
		assertTrue(CustomFileOffsetBackingStore.isDisabled());
		mockEmailNoticeProcessor.assertIsSatisfied();
		mockShutdownBean.assertIsSatisfied();
	}
	
}
