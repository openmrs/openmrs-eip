package org.openmrs.eip.fhir.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.camel.component.http.HttpClientConfigurer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(classes = OpenmrsRestConfiguration.class)
@ActiveProfiles("test")
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
