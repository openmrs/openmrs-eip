package org.openmrs.eip.app.sender;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("debeziumEventProcessor")
@Profile(SyncProfiles.SENDER)
public class DebeziumEventProcessor extends BaseQueueProcessor<DebeziumEvent> {
	
	@Override
	public String getProcessorName() {
		return "db event";
	}
	
	@Override
	public String getThreadName(DebeziumEvent event) {
		String name = event.getEvent().getTableName() + "-" + event.getEvent().getPrimaryKeyId() + "-" + event.getId();
		if (StringUtils.isNotBlank(event.getEvent().getIdentifier())) {
			name += ("-" + event.getEvent().getIdentifier());
		}
		
		return name;
	}
	
	@Override
	public String getItemKey(DebeziumEvent item) {
		return item.getEvent().getTableName() + "#" + item.getEvent().getPrimaryKeyId();
	}
	
	@Override
	public boolean processInParallel(DebeziumEvent item) {
		return item.getEvent().getSnapshot();
	}
	
	@Override
	public String getQueueName() {
		return "db-event";
	}
	
	@Override
	public String getDestinationUri() {
		return SenderConstants.URI_DBZM_EVENT_PROCESSOR;
	}
	
}
