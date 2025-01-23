package org.openmrs.eip.fhir.spring;

import static java.util.Base64.getEncoder;

import java.util.Collections;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.openmrs.eip.fhir.security.TokenCache;
import org.openmrs.eip.fhir.security.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.openmrs.eip.fhir")
public class OpenmrsRestConfiguration {
	
	@Value("${openmrs.baseUrl}")
	private String baseUrl;
	
	@Value("${openmrs.username}")
	private String openmrsUsername;
	
	@Value("${openmrs.password}")
	private String openmrsPassword;
	
	@Value("${oauth.enabled:false}")
	private boolean isOauthEnabled;
	
	public boolean isOauthEnabled() {
		return isOauthEnabled;
	}
	
	@Autowired
	private TokenCache tokenCache;
	
	@Bean
	@Qualifier("camelOpenmrsHttpClient")
	public HttpComponent camelHttpClient(CamelContext camelContext) {
		HttpComponent httpComponent = new HttpComponent();
		httpComponent.setCamelContext(camelContext);
		httpComponent.setHttpClientConfigurer(createHttpClientConfigurer());
		return httpComponent;
	}
	
	private HttpClientConfigurer createHttpClientConfigurer() {
		return clientBuilder -> {
			if (isOauthEnabled) {
				TokenInfo tokenInfo = tokenCache.getTokenInfo();
				Header header = new BasicHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
				clientBuilder.setDefaultHeaders(Collections.singleton(header));
			} else if (openmrsUsername != null && openmrsPassword != null) {
				String auth = openmrsUsername + ":" + openmrsPassword;
				String base64Auth = getEncoder().encodeToString(auth.getBytes());
				Header header = new BasicHeader("Authorization", "Basic " + base64Auth);
				clientBuilder.setDefaultHeaders(Collections.singleton(header));
			} else {
				throw new IllegalStateException("Authentication credentials are not provided");
			}
		};
	}
	
	@Bean
	CamelContextConfiguration contextRestConfiguration() {
		return new CamelContextConfiguration() {
			
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				camelContext.getComponent("http", HttpComponent.class).setHttpClientConfigurer(createHttpClientConfigurer());
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				
			}
		};
	}
}
