package org.openmrs.eip.mysql.watcher.route;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

@MockEndpoints
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WatcherInitRouteTest extends BaseWatcherRouteTest {
	
	@EndpointInject("mock:init")
	private MockEndpoint mockInit;
	
	@BeforeEach
	public void setup() throws Exception {
		mockInit.reset();
		
		camelContext.addRoutes(new WatcherInitRoute());
		
		adviceWith(camelContext, "openmrs-watcher-init-route", route -> {
			route.replaceFromWith("direct:start");
			route.weaveByToUri("openmrs-watcher:init").replace().to("mock:init");
		});
	}
	
	@Test
	public void shouldInitializeOpenmrsWatcherRoute() throws Exception {
		// Arrange
		mockInit.expectedMessageCount(1);
		
		// Act
		ProducerTemplate template = camelContext.createProducerTemplate();
		template.sendBody("direct:start", null);
		
		// Assert
		mockInit.assertIsSatisfied();
	}
	
	@Test
	public void shouldRegisteredOpenmrsInitRoute() {
		assertNotNull(camelContext.getRoute("openmrs-watcher-init-route"));
	}
}
