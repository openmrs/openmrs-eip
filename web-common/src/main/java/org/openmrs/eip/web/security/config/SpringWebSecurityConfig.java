/*
 * Add Copyright
 */
package org.openmrs.eip.web.security.config;

import static org.openmrs.eip.web.RestConstants.API_PATH;
import static org.openmrs.eip.web.RestConstants.PATH_LOGIN;
import static org.openmrs.eip.web.security.SecurityConstants.ROLE_AUTHENTICATED;
import static org.openmrs.eip.web.security.SecurityConstants.USERS_FILE;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebSecurityConfig.class);
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired(required = false)
	private H2ConsoleProperties h2ConsoleProperties;
	
	/**
	 * @see WebSecurityConfigurerAdapter#configure(HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (h2ConsoleProperties != null && h2ConsoleProperties.getEnabled()) {
			String path = h2ConsoleProperties.getPath();
			String h2UrlMapping = path + (path.endsWith("/") ? "**" : "/**");
			http.authorizeRequests().antMatchers(h2UrlMapping).permitAll();
			http.csrf().ignoringAntMatchers(h2UrlMapping);
			http.headers().frameOptions().sameOrigin();
		}
		
		http.authorizeRequests().antMatchers(PATH_LOGIN).permitAll();
		http.authorizeRequests().antMatchers("/favicon.*.ico").permitAll();
		http.authorizeRequests().antMatchers("/favicon.*.ico").permitAll();
		http.authorizeRequests().antMatchers("/*/favicon.*.ico").permitAll();
		http.authorizeRequests().antMatchers("/styles.*.css").permitAll();
		http.authorizeRequests().antMatchers("/*/styles.*.css").permitAll();
		http.authorizeRequests().antMatchers("/css/login.*.css").permitAll();
		http.httpBasic();
		
		http.authorizeRequests().anyRequest().fullyAuthenticated();
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		
		http.formLogin().loginPage(PATH_LOGIN).defaultSuccessUrl("/", true);
		http.exceptionHandling().defaultAuthenticationEntryPointFor(new CustomAuthenticationEntryPoint(),
		    new AntPathRequestMatcher(API_PATH + "**"));
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Loading and setting up user info...");
		}
		
		Properties users;
		try {
			users = PropertiesLoaderUtils.loadProperties(new FileSystemResourceLoader().getResource(USERS_FILE));
		}
		catch (FileNotFoundException e) {
			//Useful in tests
			users = PropertiesLoaderUtils.loadProperties(new ClassPathResource(USERS_FILE));
		}
		
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
