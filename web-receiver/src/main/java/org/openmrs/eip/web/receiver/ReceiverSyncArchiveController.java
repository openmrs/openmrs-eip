package org.openmrs.eip.web.receiver;

import static org.apache.camel.component.jpa.JpaConstants.JPA_PARAMETERS_HEADER;
import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;
import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
	
	private static final String PROP_DATE_RECEIVED = "dateReceived";
	
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
	
	@GetMapping(params = { PARAM_START_DATE, PARAM_END_DATE, PARAM_GRP_PROP })
	public Map<String, Object> getSyncArchives(@RequestParam(name = PARAM_START_DATE) String startDateStr,
	                                           @RequestParam(name = PARAM_END_DATE) String endDateStr,
	                                           @RequestParam(PARAM_GRP_PROP) String groupProperty)
	    throws ParseException {
		
		if (log.isDebugEnabled()) {
			log.debug("Searching receiver sync archives by start date: " + startDateStr + ", end date: " + endDateStr
			        + ", grouped by: " + groupProperty);
		}
		
		Date startDate = null;
		if (StringUtils.isNotBlank(startDateStr)) {
			startDate = RestConstants.DATE_FORMAT.parse(startDateStr);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Parsed start date: " + startDate);
		}
		
		Date endDate = null;
		if (StringUtils.isNotBlank(endDateStr)) {
			endDate = parseAndRollToEndOfDay(endDateStr);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Parsed end date: " + endDate);
		}
		
		final String queryParamStartDate = "startDate";
		final String queryParamEndDate = "endDate";
		Map<String, Object> results = new HashMap(2);
		String whereClause = StringUtils.EMPTY;
		
		Map<String, Object> paramAndValueMap = new HashMap(2);
		if (startDate != null) {
			whereClause = " WHERE e." + PROP_DATE_RECEIVED + " >= :" + queryParamStartDate;
			paramAndValueMap.put(queryParamStartDate, startDate);
		}
		
		if (endDate != null) {
			whereClause += (StringUtils.isBlank(whereClause) ? " WHERE" : " AND") + " e." + PROP_DATE_RECEIVED + " <= :"
			        + queryParamEndDate;
			paramAndValueMap.put(queryParamEndDate, endDate);
		}
		
		Integer count = producerTemplate.requestBodyAndHeader(
		    "jpa:" + getName() + "?query=SELECT count(*) FROM " + getName() + " e " + whereClause, null,
		    JPA_PARAMETERS_HEADER, paramAndValueMap, Integer.class);
		
		results.put(FIELD_COUNT, count);
		boolean group = StringUtils.isNotBlank(groupProperty);
		if (count == 0) {
			if (group) {
				results.put(FIELD_ITEMS, Collections.emptyMap());
			} else {
				results.put(FIELD_ITEMS, Collections.emptyList());
			}
			
			return results;
		}
		
		String fields = "e";
		String groupBy = "";
		if (group) {
			fields += ("." + groupProperty + ", count(*)");
			groupBy += (" GROUP BY e." + groupProperty);
		}
		
		String query = "jpa:" + getName() + "?query=SELECT " + fields + " FROM " + getName() + " e " + whereClause + groupBy;
		List<Object> items = producerTemplate.requestBodyAndHeader(query + " &maximumResults=" + DEFAULT_MAX_COUNT, null,
		    JPA_PARAMETERS_HEADER, paramAndValueMap, List.class);
		results.put(FIELD_ITEMS, items);
		
		if (group) {
			final Map<String, Integer> propCountMap = new HashMap();
			items.forEach(row -> {
				Object[] values = (Object[]) row;
				propCountMap.put(values[0].toString(), Integer.valueOf(values[1].toString()));
			});
			
			results.put(FIELD_ITEMS, propCountMap);
		}
		
		return results;
	}
	
}
