package org.openmrs.eip.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDashboardGenerator extends BaseDashboardGenerator {
	
	public TestDashboardGenerator(@Autowired ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public Dashboard generate() {
		return null;
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		return "modelClassName";
	}
	
}
