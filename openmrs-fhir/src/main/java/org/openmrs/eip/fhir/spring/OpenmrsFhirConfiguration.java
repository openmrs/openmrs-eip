package org.openmrs.eip.fhir.spring;

import org.apache.camel.CamelContext;
import org.apache.camel.component.fhir.FhirComponent;
import org.apache.camel.component.fhir.FhirConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
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
	private OpenmrsFhirOauth2 openmrsFhirOauth2;
	
	public boolean isOauthEnabled() {
		return isOauthEnabled;
	}
	
	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				FhirConfiguration fhirConfiguration = new FhirConfiguration();
				FhirContext ctx = FhirContext.forR4();
				fhirConfiguration.setServerUrl(fhirServerUrl);
				IGenericClient client = ctx.newRestfulGenericClient(fhirServerUrl);
				
				if (isOauthEnabled()) {
					client.registerInterceptor(new IClientInterceptor() {
						
						@Override
						public void interceptRequest(ca.uhn.fhir.rest.client.api.IHttpRequest theRequest) {
							theRequest.addHeader("Authorization",
							    "Bearer " + openmrsFhirOauth2.fetchAuthToken().getAccessToken());
						}
						
						@Override
						public void interceptResponse(ca.uhn.fhir.rest.client.api.IHttpResponse theResponse) {
							
						}
					});
				} else {
					if (fhirUsername != null && !fhirUsername.isBlank() && fhirPassword != null && !fhirPassword.isBlank()) {
						client.registerInterceptor(new BasicAuthInterceptor(fhirUsername, fhirPassword));
					}
				}
				fhirConfiguration.setClient(client);
				fhirConfiguration.setFhirContext(ctx);
				fhirConfiguration.setSummary("DATA");
				
				camelContext.getComponent("fhir", FhirComponent.class).setConfiguration(fhirConfiguration);
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				
			}
		};
	}
}
