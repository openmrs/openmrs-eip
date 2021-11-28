package org.openmrs.eip;

import org.openmrs.eip.app.config.AppConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import(AppConfig.class)
public class TestConfig {
	
}
