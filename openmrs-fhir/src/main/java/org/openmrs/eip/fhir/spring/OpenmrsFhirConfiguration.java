package org.openmrs.eip.fhir.spring;

import org.apache.camel.CamelContext;
import org.apache.camel.component.fhir.FhirComponent;
import org.apache.camel.component.fhir.FhirConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration bean which provides a CamelContextConfiguration to setup the configuration for the
 * camel-fhir component.
 */
@Configuration
public class OpenmrsFhirConfiguration {
	
	@Value("${eip.fhir.serverUrl}")
	private String fhirServerUrl;
	
	@Value("${eip.fhir.username}")
	private String fhirUsername;
	
	@Value("${eip.fhir.password}")
	private String fhirPassword;
	
	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				FhirComponent fhirComponent = camelContext.getComponent("fhir", FhirComponent.class);
				FhirConfiguration fhirConfiguration = fhirComponent.getConfiguration();
				fhirConfiguration.setFhirVersion("R4");
				fhirConfiguration.setServerUrl(fhirServerUrl);
				
				if (fhirUsername != null && !fhirUsername.isBlank() && fhirPassword != null && !fhirPassword.isBlank()) {
					fhirConfiguration.setUsername(fhirUsername);
					fhirConfiguration.setPassword(fhirPassword);
				}
				
				fhirComponent.setConfiguration(fhirConfiguration);
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				
			}
		};
	}
}
