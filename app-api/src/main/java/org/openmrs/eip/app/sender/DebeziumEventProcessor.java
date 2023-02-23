package org.openmrs.eip.app.sender;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BaseToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("debeziumEventProcessor")
@Profile(SyncProfiles.SENDER)
public class DebeziumEventProcessor extends BaseToCamelEndpointProcessor<DebeziumEvent> {
	
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
	public String getUniqueId(DebeziumEvent item) {
		return item.getEvent().getPrimaryKeyId();
	}
	
	@Override
	public String getQueueName() {
		return "db-event";
	}
	
	@Override
	public String getEndpointUri() {
		return SenderConstants.URI_DBZM_EVENT_PROCESSOR;
	}
	
	@Override
	public String getLogicalType(DebeziumEvent item) {
		return item.getEvent().getTableName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfTablesInHierarchy(logicalType);
	}
	
}
