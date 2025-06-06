package org.openmrs.eip.fhir.spring;

import org.apache.camel.CamelContext;
import org.apache.camel.component.fhir.FhirComponent;
import org.apache.camel.component.fhir.FhirConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.fhir.security.interceptor.Oauth2Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

/**
 * Configuration bean which provides a CamelContextConfiguration to setup the configuration for the
 * camel-fhir component.
 */
@Configuration
@ComponentScan("org.openmrs.eip.fhir")
public class OpenmrsFhirConfiguration {
	
	@Value("${eip.fhir.serverUrl}")
	private String fhirServerUrl;
	
	@Value("${eip.fhir.username}")
	private String fhirUsername;
	
	@Value("${eip.fhir.password}")
	private String fhirPassword;
	
	@Value("${oauth.enabled:false}")
	private boolean isOauthEnabled;
	
	@Autowired
	private Oauth2Interceptor oauth2Interceptor;
	
	public boolean isOauthEnabled() {
		return isOauthEnabled;
	}
	
	@Bean
	@Qualifier("openmrsFhirClient")
	IGenericClient openmrsFhirClient() {
		IGenericClient client = FhirContext.forR4().newRestfulGenericClient(fhirServerUrl);
		client.setSummary(SummaryEnum.TRUE);
		if (isOauthEnabled) {
			client.registerInterceptor(oauth2Interceptor);
		} else if (StringUtils.isNotBlank(fhirUsername) && StringUtils.isNotBlank(fhirPassword)) {
			client.registerInterceptor(new BasicAuthInterceptor(fhirUsername, fhirPassword));
		} else {
			throw new IllegalStateException("Authentication credentials are not provided");
		}
		return client;
	}
	
	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				FhirConfiguration fhirConfiguration = new FhirConfiguration();
				fhirConfiguration.setServerUrl(fhirServerUrl);
				fhirConfiguration.setClient(openmrsFhirClient());
				fhirConfiguration.setFhirContext(FhirContext.forR4());
				fhirConfiguration.setSummary("DATA");
				camelContext.getComponent("fhir", FhirComponent.class).setConfiguration(fhirConfiguration);
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				
			}
		};
	}
}
