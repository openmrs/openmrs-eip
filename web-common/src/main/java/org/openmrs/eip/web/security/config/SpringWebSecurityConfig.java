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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebSecurityConfig.class);
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers(PATH_LOGIN).permitAll());
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/favicon.*.ico").permitAll());
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/*/favicon.*.ico").permitAll());
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/styles.*.css").permitAll());
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/*/styles.*.css").permitAll());
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/css/login.*.css").permitAll());
		http.httpBasic(Customizer.withDefaults());
		
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().fullyAuthenticated());
		http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
		http.formLogin(formLogin -> formLogin.loginPage(PATH_LOGIN).defaultSuccessUrl("/", true));
		http.exceptionHandling(exHandler -> exHandler.defaultAuthenticationEntryPointFor(
		    new CustomAuthenticationEntryPoint(), new AntPathRequestMatcher(API_PATH + "**")));
		
		return http.build();
	}
	
	@Bean
	protected AuthenticationManager inMemoryUserDetailsManager(AuthenticationManagerBuilder auth,
	                                                           PasswordEncoder passwordEncoder)
	    throws Exception {
		
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
		
		return auth.build();
	}
	
}
