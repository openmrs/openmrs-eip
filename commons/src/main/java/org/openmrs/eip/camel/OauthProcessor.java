package org.openmrs.eip.camel;

import static org.openmrs.eip.Constants.HTTP_HEADER_AUTH;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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
	
	public static final String OAUTH_URI = "direct:oauth";
	
	public static final String HTTP_AUTH_SCHEME = "Bearer";
	
	public static final String FIELD_TOKEN = "access_token";
	
	public static final String FIELD_EXPIRES_IN = "expires_in";
	
	public static final String FIELD_TYPE = "token_type";
	
	private OauthToken oauthToken;
	
	@Produce
	private ProducerTemplate producerTemplate;
	
	@Value("${oauth.enabled:false}")
	private boolean isOauthEnabled;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (!isOauthEnabled) {
			if (logger.isDebugEnabled()) {
				logger.debug("Oauth is not enabled, skip fetching token");
			}
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
				Map response = producerTemplate.requestBody(OAUTH_URI, null, Map.class);
				
				Object type = response.get(FIELD_TYPE);
				if (type == null || !HTTP_AUTH_SCHEME.equalsIgnoreCase(type.toString())) {
					throw new EIPException("Unsupported oauth token type: " + type);
				}
				
				long expiresAt = currentSeconds + Long.valueOf(response.get(FIELD_EXPIRES_IN).toString());
				
				oauthToken = new OauthToken(response.get(FIELD_TOKEN).toString(), expiresAt);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Using cached oauth token");
				}
				
				//TODO verify if token is still valid, e.g. in the event the IDP was restarted or if token is revoked
			}
			
			exchange.getIn().setHeader(HTTP_HEADER_AUTH, HTTP_AUTH_SCHEME + " " + oauthToken.getAccessToken());
		}
	}
	
}
