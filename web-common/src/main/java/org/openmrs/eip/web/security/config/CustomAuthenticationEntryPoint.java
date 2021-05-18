/*
 * Add Copyright
 */
package org.openmrs.eip.web.security.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * This custom {@link AuthenticationEntryPoint} sets a 401 error code on the response object for a
 * rejected unauthenticated request, this allows the 401 status to propagate back to the UI angular
 * scripts otherwise the browser performs a redirect to the login page set by spring's
 * {@link org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint} before
 * it reaches the angular response interceptors.
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * @see AuthenticationEntryPoint#commence(HttpServletRequest, HttpServletResponse,
	 *      AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
	    throws IOException, ServletException {
		
		if (log.isDebugEnabled()) {
			log.debug("Setting http status code to " + HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated or Session has expired");
		
	}
}
