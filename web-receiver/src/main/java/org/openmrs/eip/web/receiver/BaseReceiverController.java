package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseReceiverController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(BaseReceiverController.class);
	
	public Object getGroupedItems(String groupProperty) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching items of type " + getName() + " grouped by " + groupProperty);
		}
		
		Map<String, Object> results = new HashMap(2);
		Integer count = getAllCount();
		results.put(FIELD_COUNT, count);
		
		if (count > 0) {
			List<Object[]> items = producerTemplate.requestBody("jpa:" + getName() + "?query=SELECT e." + groupProperty
			        + ", count(*) FROM " + getName() + " e GROUP BY e." + groupProperty,
			    null, List.class);
			final Map<String, Integer> propCountMap = new HashMap();
			items.forEach(values -> {
				propCountMap.put(values[0].toString(), Integer.valueOf(values[1].toString()));
			});
			
			results.put(FIELD_ITEMS, propCountMap);
		} else {
			results.put(FIELD_ITEMS, Collections.emptyMap());
		}
		
		return results;
	}
	
}
