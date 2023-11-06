package org.openmrs.eip.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDashboardHelper extends BaseDashboardHelper {
	
	public TestDashboardHelper(@Autowired ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		return "modelClassName";
	}
	
}
