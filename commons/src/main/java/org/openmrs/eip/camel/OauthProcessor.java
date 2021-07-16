package org.openmrs.eip.camel;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.Constants;
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
	
	public static final String FIELD_REFRESH_EXPIRES_IN = "refresh_expires_in";
	
	public static final String FIELD_REFRESH_TOKEN = "refresh_token";
	
	public static final String FIELD_TYPE = "token_type";
	
	@Produce
	private ProducerTemplate producerTemplate;
	
	@Value("${oauth.enabled}")
	private boolean isOauthEnabled;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Checking if Oauth is enabled");
		if (!isOauthEnabled) {
			logger.info("Oauth is not enabled, skip fetching token");
			return;
		}
		
		Map response = producerTemplate.requestBody(OAUTH_URI, null, Map.class);
		
		exchange.getIn().setHeader(Constants.HTTP_HEADER_AUTH, HTTP_AUTH_SCHEME + " " + response.get(FIELD_TOKEN));
	}
	
}
