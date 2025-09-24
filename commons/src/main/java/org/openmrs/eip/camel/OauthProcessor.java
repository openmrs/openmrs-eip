package org.openmrs.eip.camel;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.OauthToken;
import org.openmrs.eip.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This processor checks if oauth is enabled and if it is, it fetches the auth token for the
 * configured client from the configured identity provider and sets the token as the Authorization
 * header value.
 */
@Component("oauthProcessor")
public class OauthProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(OauthProcessor.class);
	
	public static final String HTTP_AUTH_SCHEME = "Bearer";
	
	public static final String FIELD_TOKEN = "access_token";
	
	public static final String FIELD_EXPIRES_IN = "expires_in";
	
	public static final String FIELD_TYPE = "token_type";
	
	private OauthToken oauthToken;
	
	private OauthAuthenticator authenticator;
	
	@Value("${oauth.enabled:false}")
	private boolean isOauthEnabled;
	
	@Value("${oauth.authenticator.use.camel:true}")
	private boolean useCamelAuthenticator;
	
	@Value("${oauth.access.token.uri:}")
	private String tokenUrl;
	
	@Value("${oauth.client.id:}")
	private String clientId;
	
	@Value("${oauth.client.secret:}")
	private char[] clientSecret;
	
	@Value("${oauth.client.scope:}")
	private String scope;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (!isOauthEnabled) {
			if (logger.isDebugEnabled()) {
				logger.debug("Oauth is not enabled, skip fetching token");
			}
			
			exchange.getIn().setBody(null);
			
			return;
		}
		
		synchronized (this) {
			if (oauthToken == null || oauthToken.isExpired(LocalDateTime.now())) {
				if (oauthToken != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Cached oauth token is expired, fetching a new one");
					}
				}
				
				long currentSeconds = Utils.getCurrentSeconds();
				Map<String, Object> response = getAuthenticator().authenticate();
				Object type = response.get(FIELD_TYPE);
				if (type == null || !HTTP_AUTH_SCHEME.equalsIgnoreCase(type.toString())) {
					throw new EIPException("Unsupported oauth token type: " + type);
				}
				
				// Subtracting 10s such that renewing of token happens 10s before it actually expires
				long expiresAt = currentSeconds + Long.valueOf(response.get(FIELD_EXPIRES_IN).toString()) - 10;
				
				oauthToken = new OauthToken(response.get(FIELD_TOKEN).toString(), expiresAt);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Using cached oauth token");
				}
				
				//TODO verify if token is still valid, e.g. in the event the IDP was restarted or if token is revoked
			}
			
			exchange.getIn().setBody(HTTP_AUTH_SCHEME + " " + oauthToken.getAccessToken());
		}
	}
	
	private OauthAuthenticator getAuthenticator() {
		if (authenticator == null) {
			synchronized (this) {
				if (authenticator == null) {
					if (useCamelAuthenticator) {
						logger.info("Using camel route for oauth2 authentication");
						authenticator = AppContext.getBean(CamelOauthAuthenticator.class);
					} else {
						logger.info("Using HTTP client for oauth2 authentication");
						authenticator = new HttpOauthAuthenticator(tokenUrl, clientId, clientSecret, scope);
					}
				}
			}
		}
		
		return authenticator;
	}
	
}
