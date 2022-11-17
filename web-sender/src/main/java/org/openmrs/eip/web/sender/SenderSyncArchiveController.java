package org.openmrs.eip.web.sender;

import static org.apache.camel.component.jpa.JpaConstants.JPA_PARAMETERS_HEADER;
import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;
import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/sender/archive")
public class SenderSyncArchiveController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(SenderSyncArchiveController.class);
	
	private static final String PROP_EVENT_DATE = "eventDate";
	
	@Override
	public Class<?> getClazz() {
		return SenderSyncArchive.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sender sync archives");
		}
		
		return doGetAll();
	}
	
	@GetMapping(params = { PARAM_START_DATE, PARAM_END_DATE })
	public Map<String, Object> searchByEventDate(@RequestParam(name = PARAM_START_DATE) String startDateStr,
	                                             @RequestParam(name = PARAM_END_DATE) String endDateStr)
	    throws ParseException {
		
		if (log.isDebugEnabled()) {
			log.debug("Searching sender sync archives by start date: " + startDateStr + ", end date: " + endDateStr);
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
			whereClause = " WHERE e." + PROP_EVENT_DATE + " >= :" + queryParamStartDate;
			paramAndValueMap.put(queryParamStartDate, startDate);
		}
		
		if (endDate != null) {
			whereClause += (StringUtils.isBlank(whereClause) ? " WHERE" : " AND") + " e." + PROP_EVENT_DATE + " <= :"
			        + queryParamEndDate;
			paramAndValueMap.put(queryParamEndDate, endDate);
		}
		
		Integer count = producerTemplate.requestBodyAndHeader(
		    "jpa:" + getName() + "?query=SELECT count(*) FROM " + getName() + " e " + whereClause, null,
		    JPA_PARAMETERS_HEADER, paramAndValueMap, Integer.class);
		
		if (count == 0) {
			results.put(FIELD_COUNT, 0);
			results.put(FIELD_ITEMS, Collections.emptyList());
			return results;
		}
		
		List<Object> items = producerTemplate.requestBodyAndHeader("jpa:" + getName() + "?query=SELECT e FROM " + getName()
		        + " e " + whereClause + " &maximumResults=" + DEFAULT_MAX_COUNT,
		    null, JPA_PARAMETERS_HEADER, paramAndValueMap, List.class);
		
		results.put(FIELD_COUNT, count);
		results.put(FIELD_ITEMS, items);
		
		return results;
	}
	
}
