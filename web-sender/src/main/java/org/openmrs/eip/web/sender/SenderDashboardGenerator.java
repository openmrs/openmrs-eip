package org.openmrs.eip.web.sender;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.BaseDashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.SENDER)
public class SenderDashboardGenerator extends BaseDashboardGenerator {
	
	private static final String ERROR_ENTITY_NAME = SenderRetryQueueItem.class.getSimpleName();
	
	private static final String EVENT_ENTITY_NAME = DebeziumEvent.class.getSimpleName();
	
	protected CamelContext camelContext;
	
	@Autowired
	public SenderDashboardGenerator(CamelContext camelContext, ProducerTemplate producerTemplate) {
		super(producerTemplate);
		this.camelContext = camelContext;
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		if (EVENT_ENTITY_NAME.equals(entityType) || ERROR_ENTITY_NAME.equals(entityType)) {
			return "event.tableName";
		}
		
		return "tableName";
	}
	
}
