package org.openmrs.eip.web.contoller;

import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRestController {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseRestController.class);
	
	@Autowired
	protected ProducerTemplate producerTemplate;
	
	@Autowired
	protected CamelContext camelContext;
	
	protected Integer getAllCount() {
		return producerTemplate.requestBody("jpa:" + getName() + "?query=SELECT count(*) FROM " + getName(), null,
		    Integer.class);
	}
	
	public Map<String, Object> doGetAll() {
		Map<String, Object> results = new HashMap(2);
		Integer count = getAllCount();
		results.put(FIELD_COUNT, count);
		
		if (count > 0) {
			List<Object> items = producerTemplate.requestBody(
			    "jpa:" + getName() + "?query=SELECT c FROM " + getName() + " c &maximumResults=" + DEFAULT_MAX_COUNT, null,
			    List.class);
			
			results.put(FIELD_ITEMS, items);
		} else {
			results.put(FIELD_ITEMS, Collections.emptyList());
		}
		
		return results;
	}
	
	public Object doGet(Long id) {
		return producerTemplate.requestBody(
		    "jpa:" + getName() + "?query=SELECT c FROM " + getName() + " c WHERE c.id = " + id, null, getClazz());
	}
	
	public static Date parseAndRollToEndOfDay(String date) {
		LocalDateTime ldt = LocalDate.parse(date, RestConstants.DATE_FORMATTER).atTime(LocalTime.MAX);
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public String getName() {
		return getClazz().getSimpleName();
	}
	
	public abstract Class<?> getClazz();
	
}
