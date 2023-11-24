/*
 * Add Copyright
 */
package org.openmrs.eip.web.security.config;

import static org.openmrs.eip.web.RestConstants.API_PATH;
import static org.openmrs.eip.web.RestConstants.PATH_LOGIN;
import static org.openmrs.eip.web.security.SecurityConstants.USERS_FILE;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.eip.web.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebSecurityConfig.class);
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher(PATH_LOGIN)).permitAll());
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/favicon.*.ico")).permitAll());
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/*/favicon.*.ico")).permitAll());
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/styles.*.css")).permitAll());
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/*/styles.*.css")).permitAll());
		http.authorizeHttpRequests(
		    authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/css/login.*.css")).permitAll());
		http.httpBasic(Customizer.withDefaults());
		
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().fullyAuthenticated());
		http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
		http.formLogin(formLogin -> formLogin.loginPage(PATH_LOGIN).defaultSuccessUrl("/", true));
		http.exceptionHandling(exHandler -> exHandler.defaultAuthenticationEntryPointFor(
		    new CustomAuthenticationEntryPoint(), new AntPathRequestMatcher(API_PATH + "**")));
		
		return http.build();
	}
	
	@Bean
	protected UserDetailsService inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) throws Exception {
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
		
		List<UserDetails> userDetails = new ArrayList<>();
		for (Map.Entry<Object, Object> entry : users.entrySet()) {
			UserDetails user = User.builder().username(entry.getKey().toString())
			        .passwordEncoder(pass -> passwordEncoder.encode(entry.getValue().toString()))
			        .roles(SecurityConstants.ROLE_AUTHENTICATED).build();
			userDetails.add(user);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Done setting up user info!");
		}
		
		return new InMemoryUserDetailsManager(userDetails);
	}
	
}
