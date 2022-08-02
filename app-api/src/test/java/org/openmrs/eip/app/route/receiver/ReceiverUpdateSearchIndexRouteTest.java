package org.openmrs.eip.app.route.receiver;

import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_UPDATE_SEARCH_INDEX;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_UPDATE_SEARCH_INDEX;
import static org.openmrs.eip.app.route.receiver.ReceiverUpdateSearchIndexRouteTest.OPENMRS_PASS;
import static org.openmrs.eip.app.route.receiver.ReceiverUpdateSearchIndexRouteTest.OPENMRS_URL;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_USER;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.openmrs.eip.component.Constants;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;

@TestPropertySource(properties = Constants.PROP_OPENMRS_URL + "=" + OPENMRS_URL)
@TestPropertySource(properties = Constants.PROP_OPENMRS_PASS + "=" + OPENMRS_PASS)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_UPDATE_SEARCH_INDEX + "=DEBUG")
public class ReceiverUpdateSearchIndexRouteTest extends BaseReceiverRouteTest {
	
	protected static final String OPENMRS_URL = "mock:url";
	
	protected static final String OPENMRS_USER = "admin";
	
	protected static final String OPENMRS_PASS = "test";
	
	protected static final String SEARCH_INDEX_URL = OPENMRS_URL
	        + "/ws/rest/v1/searchindexupdate?authMethod=Basic&authUsername=" + OPENMRS_USER + "&authPassword="
	        + OPENMRS_PASS;
	
	@EndpointInject(SEARCH_INDEX_URL)
	private MockEndpoint mockIndexUpdateEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(env, PROP_OPENMRS_USER + "=" + OPENMRS_USER);
		return ROUTE_ID_UPDATE_SEARCH_INDEX;
	}
	
	@Test
	public void shouldCallOpenmrsToUpdateTheSearchIndex() throws Exception {
		mockIndexUpdateEndpoint.expectedMessageCount(1);
		mockIndexUpdateEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
		mockIndexUpdateEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "POST");
		
		producerTemplate.send(URI_UPDATE_SEARCH_INDEX, new DefaultExchange(camelContext));
		
		mockIndexUpdateEndpoint.assertIsSatisfied();
	}
	
}
