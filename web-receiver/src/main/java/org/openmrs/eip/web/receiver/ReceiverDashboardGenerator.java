package org.openmrs.eip.web.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.BaseDashboardGenerator;
import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.controller.DashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverDashboardGenerator extends BaseDashboardGenerator {
	
	@Autowired
	public ReceiverDashboardGenerator(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		return "modelClassName";
	}
	
	/**
	 * @see DashboardGenerator#generate()
	 */
	@Override
	public Dashboard generate() {
		return null;
	}
	
}
