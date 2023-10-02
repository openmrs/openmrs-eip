package org.openmrs.eip.web;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.web.controller.DashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class TestWebConfig {
	
	@Bean
	public DashboardGenerator testDashboardGenerator(@Autowired(required = false) ProducerTemplate producerTemplate) {
		return new DelegatingDashboardGenerator(producerTemplate);
	}
	
}
