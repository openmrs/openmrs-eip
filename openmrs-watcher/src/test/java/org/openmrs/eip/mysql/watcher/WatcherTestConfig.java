package org.openmrs.eip.mysql.watcher;

import org.springframework.context.annotation.Bean;

public class WatcherTestConfig {
	
	@Bean
	public WatcherPropertiesBeanPostProcessor getWatcherPropertiesBeanPostProcessor() {
		return new WatcherPropertiesBeanPostProcessor();
	}
	
}
