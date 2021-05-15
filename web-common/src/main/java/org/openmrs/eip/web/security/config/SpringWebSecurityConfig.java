/*
 * Add Copyright
 */
package org.openmrs.eip.web.security.config;

import static org.openmrs.eip.web.security.SecurityConstants.ROLE_AUTHENTICATED;
import static org.openmrs.eip.web.security.SecurityConstants.USERS_FILE;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebSecurityConfig.class);
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * @see WebSecurityConfigurerAdapter#configure(HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().fullyAuthenticated();
		http.formLogin();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Loading and setting up user info...");
		}
		
		Properties users = PropertiesLoaderUtils.loadProperties(new FileSystemResourceLoader().getResource(USERS_FILE));
		InMemoryUserDetailsManagerConfigurer configurer = auth.inMemoryAuthentication();
		for (Map.Entry<Object, Object> entry : users.entrySet()) {
			String encodedPass = passwordEncoder.encode(entry.getValue().toString());
			configurer.withUser(entry.getKey().toString()).password(encodedPass).roles(ROLE_AUTHENTICATED);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Done setting up user info!");
		}
	}
	
}
