package org.openmrs.eip.web.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.BaseDashboardHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverDashboardHelper extends BaseDashboardHelper {
	
	@Autowired
	public ReceiverDashboardHelper(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		return "modelClassName";
	}
	
}
