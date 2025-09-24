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

/**
 * An instance of this interface authenticates a client with an authentication provider.
 */
public interface OauthAuthenticator {
	
	/**
	 * Authenticates the client with the authentication provider and returns the auth token as a Map.
	 * 
	 * @return Map
	 */
	Map<String, Object> authenticate();
	
}
