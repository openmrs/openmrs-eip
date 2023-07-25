package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PATH_VAR;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.PATH_RECEIVER_SYNC_MSG)
public class ReceiverSyncMessageController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverSyncMessageController.class);
	
	@Override
	public Class<?> getClazz() {
		return SyncMessage.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync messages");
		}
		
		return doGetAll();
	}
	
	@GetMapping("/{" + PATH_VAR + "}")
	public Object get(@PathVariable(PATH_VAR) Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync messages with id: " + id);
		}
		
		return doGet(id);
	}
	
	@GetMapping(params = PARAM_GRP_PROP)
	public Object getGroupedSyncMessages(@RequestParam(PARAM_GRP_PROP) String groupProperty) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync messages grouped by " + groupProperty);
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
