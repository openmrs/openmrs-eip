package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;
import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.PATH_RECEIVER_ARCHIVE)
public class ReceiverSyncArchiveController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverSyncArchiveController.class);
	
	@Override
	public Class<?> getClazz() {
		return ReceiverSyncArchive.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync archives");
		}
		
		return doGetAll();
	}
	
	@GetMapping(params = { PARAM_START_DATE, PARAM_END_DATE })
	public Map<String, Object> searchByDateReceived(@RequestParam(name = PARAM_START_DATE) String startDate,
	                                                @RequestParam(name = PARAM_END_DATE) String endDate)
	    throws ParseException {
		
		if (log.isDebugEnabled()) {
			log.debug("Searching receiver sync archives by start date: " + startDate + ", end date: " + endDate);
		}
		
		return doSearchByDate("dateReceived", startDate, endDate);
	}
	
	@GetMapping(params = PARAM_GRP_PROP)
	public Object getGroupedArchives(@RequestParam(PARAM_GRP_PROP) String groupProperty) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync archives grouped by " + groupProperty);
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
