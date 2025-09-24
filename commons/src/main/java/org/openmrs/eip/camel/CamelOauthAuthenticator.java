/*
 * Copyright (C) Amiyul LLC - All Rights Reserved
 *
 * This source code is protected under international copyright law. All rights
 * reserved and protected by the copyright holder.
 *
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holder. If you encounter this file and do not have
 * permission, please contact the copyright holder and delete this file.
 */
package org.openmrs.eip.camel;

import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

/**
 * An authenticator that calls the oauth camel route to authenticate the client with the provider.
 * Note that this can only be used if the client needs to be authenticated after the spring and
 * camel contexts are created otherwise use the {@link HttpOauthAuthenticator}.
 */
@Component
public class CamelOauthAuthenticator implements OauthAuthenticator {
	
	public static final String OAUTH_URI = "direct:oauth";
	
	@Produce
	private ProducerTemplate producerTemplate;
	
	@Override
	public Map<String, Object> authenticate() throws Exception {
		return producerTemplate.requestBody(OAUTH_URI, null, Map.class);
	}
	
}
