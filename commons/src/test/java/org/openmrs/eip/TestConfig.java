package org.openmrs.eip;

import org.openmrs.eip.config.CommonConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import({ CommonConfig.class })
public class TestConfig {
	
}
