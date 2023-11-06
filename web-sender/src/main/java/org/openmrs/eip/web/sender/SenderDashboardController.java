package org.openmrs.eip.web.sender;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
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
	
	private static final String ERROR_ENTITY_NAME = SenderRetryQueueItem.class.getSimpleName();
	
	private static final String ERR_QUERY = "jpa:" + ERROR_ENTITY_NAME + "?query=SELECT exceptionType, count(*) FROM "
	        + ERROR_ENTITY_NAME + " GROUP BY exceptionType";
	
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
	
	@GetMapping(SenderRestConstants.PATH_NAME_ERR_DETAILS)
	public Map<String, Object> getErrorDetails() {
		int activeMqRelatedErrCount = 0;
		int mostEncounteredErrCount = 0;
		Set<String> mostEncounteredErrors = new HashSet<>();
		Map<String, Integer> exceptionCountMap = new HashMap<>();
		List<String[]> items = on(camelContext).to(ERR_QUERY).request(List.class);
		
		for (Object[] values : items) {
			final String exception = values[0].toString();
			final int count = Integer.valueOf(values[1].toString());
			exceptionCountMap.put(exception, count);
			
			try {
				if (ActiveMQException.class.isAssignableFrom(getClass().getClassLoader().loadClass(exception))) {
					activeMqRelatedErrCount += count;
				}
			}
			catch (ClassNotFoundException e) {
				throw new EIPException("Failed to load exception class " + exception, e);
			}
			
			if (count >= mostEncounteredErrCount) {
				if (count > mostEncounteredErrCount) {
					mostEncounteredErrors.clear();
				}
				
				mostEncounteredErrCount = count;
				mostEncounteredErrors.add(exception);
			}
		}
		
		Map<String, Object> errors = new HashMap();
		errors.put("activeMqRelatedErrorCount", activeMqRelatedErrCount);
		errors.put("mostEncounteredErrors", mostEncounteredErrors);
		errors.put("exceptionCountMap", exceptionCountMap);
		
		return errors;
	}
	
}
