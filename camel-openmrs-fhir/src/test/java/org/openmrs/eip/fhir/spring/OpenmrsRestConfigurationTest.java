package org.openmrs.eip.fhir.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.camel.component.http.HttpClientConfigurer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(classes = OpenmrsRestConfiguration.class)
@TestPropertySource(properties = { "eip.fhir.resources=Encounter", "openmrs.eip.log.level=DEBUG",
        "eip.fhir.serverUrl=http://localhost:8080/openmrs/ws/fhir2/R4", "eip.fhir.username=admin",
        "eip.fhir.password=password", "openmrs.baseUrl=http://localhost:8080/openmrs", "openmrs.username=admin",
        "openmrs.password=password", "eip.test.order.concept.uuid=52a447d3-a64a-11e3-9aeb-50e549534c5e",
        "eip.imaging.order.concept.uuid=8d2aff07-55e6-4a4a-8878-72b9eb36a3b8",
        "eip.procedure.order.concept.uuid=67a92e56-0f88-11ea-8d71-362b9e155667",
        "eip.supplyrequest.order.concept.uuid=67a92bd6-0f88-11ea-8d71-362b9e155667",
        "eip.drug.order.concept.uuid=131168f4-15f5-102d-96e4-000c29c2a5d7" })
public class OpenmrsRestConfigurationTest {
	
	@Autowired
	Environment environment;
	
	@Autowired
	OpenmrsRestConfiguration openmrsRestConfiguration;
	
	@Test
	void shouldCreateHttpClientConfigurer() {
		// verify username and password are set correctly
		assertEquals("http://localhost:8080/openmrs", environment.getProperty("openmrs.baseUrl"));
		assertEquals("admin", environment.getProperty("openmrs.username"));
		assertEquals("password", environment.getProperty("openmrs.password"));
		
		HttpClientConfigurer httpClientConfigurer = openmrsRestConfiguration.createHttpClientConfigurer();
		assertNotNull(httpClientConfigurer);
	}
}
