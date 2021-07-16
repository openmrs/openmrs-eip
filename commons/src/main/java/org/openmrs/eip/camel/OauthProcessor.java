package org.openmrs.eip.camel;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.openmrs.eip.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This processor checks if oauth is enabled and if it is, it fetches the auth token for the
 * configured client from the configured identity provider and sets the token as the Authorization
 * header value.
 */
@Component("oauthProcessor")
public class OauthProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(OauthProcessor.class);
	
	protected static final String OAUTH_URI = "direct:oauth";
	
	public static final String HTTP_AUTH_SCHEME = "Bearer";
	
	protected static final String FIELD_TOKEN = "access_token";
	
	protected static final String FIELD_EXPIRES_IN = "expires_in";
	
	protected static final String FIELD_REFRESH_EXPIRES_IN = "refresh_expires_in";
	
	protected static final String FIELD_REFRESH_TOKEN = "refresh_token";
	
	protected static final String FIELD_TYPE = "token_type";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.info("Checking if Oauth is enabled");
		}
		
		Map response = (Map) DefaultFluentProducerTemplate.on(exchange.getContext()).to(OAUTH_URI).request();
		
		exchange.getIn().setHeader(Constants.HTTP_HEADER_AUTH, HTTP_AUTH_SCHEME + " " + response.get(FIELD_TOKEN));
	}
	
}
