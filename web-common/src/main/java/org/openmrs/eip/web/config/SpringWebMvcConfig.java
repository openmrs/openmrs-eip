package org.openmrs.eip.web.config;

import static org.openmrs.eip.web.security.SecurityConstants.DEFAULT_LOCALE_PROP_NAME;
import static org.openmrs.eip.web.security.SecurityConstants.LOCALE_FILE;

import java.util.Locale;
import java.util.Properties;

import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@EnableWebMvc
public class SpringWebMvcConfig implements WebMvcConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebMvcConfig.class);
	
	@Bean
	public LocaleResolver customLocaleResolver() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Loading locale configuration file...");
		}
		
		Properties localeProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource(LOCALE_FILE));
		String locale = localeProps.getProperty(DEFAULT_LOCALE_PROP_NAME);
		if (log.isDebugEnabled()) {
			log.debug("Setting default locale to: " + locale);
		}
		
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(new Locale(locale));
		
		return slr;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
	
	@Bean("messageSource")
	public MessageSource getWebMessageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("messages");
		ms.setDefaultEncoding("UTF-8");
		
		return ms;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(RestConstants.PATH_LOGIN);
	}
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("view/", ".jsp");
	}
	
}
