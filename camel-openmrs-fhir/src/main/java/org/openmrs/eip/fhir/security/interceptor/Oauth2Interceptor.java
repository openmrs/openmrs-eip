package org.openmrs.eip.fhir.security.interceptor;

import org.openmrs.eip.fhir.security.OpenmrsFhirOauth2;
import org.openmrs.eip.fhir.security.TokenCache;
import org.openmrs.eip.fhir.security.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

@Component
public class Oauth2Interceptor implements IClientInterceptor {
	
	@Autowired
	private OpenmrsFhirOauth2 openmrsFhirOauth2;
	
	@Autowired
	private TokenCache tokenCache;
	
	@Override
	public void interceptRequest(IHttpRequest iHttpRequest) {
		TokenInfo tokenInfo = tokenCache.getTokenInfo();
		iHttpRequest.addHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
	}
	
	@Override
	public void interceptResponse(IHttpResponse iHttpResponse) {
		
	}
}
