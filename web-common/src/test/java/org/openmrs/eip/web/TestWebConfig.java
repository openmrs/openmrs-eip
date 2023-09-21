package org.openmrs.eip.web;

import org.openmrs.eip.web.controller.DashboardGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class TestWebConfig {
	
	@Bean
	public DashboardGenerator testDashboardGenerator() {
		return new MockDashboardGenerator();
	}
	
}
