package org.openmrs.eip.fhir.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.eip.Constants.PROP_WATCHED_TABLES;
import static org.openmrs.eip.fhir.Constants.URI_FHIR_ROUTER;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT_DESTINATIONS;

import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

/**
 * We have some slightly gnarly code to convert the {@code eip.fhir.resources} property to some
 * properties for the watcher. Here we just ensure that this gets configured correctly if the
 * {@link OpenmrsFhirConfiguration} is loaded.
 */
@SpringBootTest
@ContextConfiguration(classes = OpenmrsFhirConfiguration.class)
@TestPropertySource(properties = { "eip.fhir.resources=Encounter", "openmrs.eip.log.level=DEBUG",
        "eip.fhir.serverUrl=http://localhost:8080/openmrs/ws/fhir2/R4", "eip.fhir.username=admin",
        "eip.fhir.password=password", "openmrs.baseUrl=http://localhost:8080/openmrs", "openmrs.username=admin",
        "openmrs.password=password", "eip.test.order.concept.uuid=52a447d3-a64a-11e3-9aeb-50e549534c5e",
        "eip.imaging.order.concept.uuid=8d2aff07-55e6-4a4a-8878-72b9eb36a3b8",
        "eip.procedure.order.concept.uuid=67a92e56-0f88-11ea-8d71-362b9e155667",
        "eip.supplyrequest.order.concept.uuid=67a92bd6-0f88-11ea-8d71-362b9e155667",
        "eip.drug.order.concept.uuid=131168f4-15f5-102d-96e4-000c29c2a5d7" })
public class OpenmrsFhirConfigurationTest {
	
	@Autowired
	Environment environment;
	
	@Autowired
	private IGenericClient openmrsFhirClient;
	
	@Test
	void shouldConfigureContextProperly() {
		assertThat(environment.getProperty(PROP_WATCHED_TABLES), equalTo(String.join(",", FhirResource.ENCOUNTER.tables())));
		assertThat(environment.getProperty(PROP_EVENT_DESTINATIONS), equalTo(URI_FHIR_ROUTER));
	}
	
	@Test
	void shouldRegisterIGenericClientBean() {
		assertNotNull(openmrsFhirClient);
	}
	
	@Test
	void shouldRegisterBasicAuthInterceptorWithCorrectUsernameAndPassword() {
		// verify username and password are set correctly
		assertEquals("admin", environment.getProperty("eip.fhir.username"));
		assertEquals("password", environment.getProperty("eip.fhir.password"));
		
		var interceptors = openmrsFhirClient.getInterceptorService().getAllRegisteredInterceptors();
		// verify BasicAuthInterceptor is registered
		assertNotNull(interceptors);
		assertTrue(interceptors.stream().anyMatch(BasicAuthInterceptor.class::isInstance));
	}
}
