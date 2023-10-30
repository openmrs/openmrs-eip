package org.openmrs.eip.web.sender;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile(SyncProfiles.SENDER)
@RequestMapping(SenderRestConstants.RES_SENDER_DASHBOARD)
public class SenderDashboardController {
	
	private static final Logger log = LoggerFactory.getLogger(SenderDashboardController.class);
	
	private static final String SYNC_ENTITY_NAME = SenderSyncMessage.class.getSimpleName();
	
	protected ProducerTemplate producerTemplate;
	
	protected CamelContext camelContext;
	
	public SenderDashboardController(ProducerTemplate producerTemplate, CamelContext camelContext) {
		this.producerTemplate = producerTemplate;
		this.camelContext = camelContext;
	}
	
	@GetMapping(SenderRestConstants.PATH_NAME_COUNT_BY_STATUS)
	public Map<Object, Object> getSyncCountByStatus() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sync item count by status");
		}
		
		final Map statusCountMap = new HashMap(2);
		
		List<Object[]> items = on(camelContext).to(
		    "jpa:" + SYNC_ENTITY_NAME + "?query=SELECT status, count(*) FROM " + SYNC_ENTITY_NAME + " GROUP BY status")
		        .request(List.class);
		
		items.forEach(values -> {
			statusCountMap.put(values[0], values[1]);
		});
		
		return statusCountMap;
	}
	
}
