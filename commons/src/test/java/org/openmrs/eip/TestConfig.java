package org.openmrs.eip;

import org.openmrs.eip.app.config.AppConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(AppConfig.class)
public class TestConfig {
	
}
