package org.openmrs.eip.web.sender;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
	
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
	
	private static final String EVENT_DATE = "eventDate";
	
	@Override
	public Class<?> getClazz() {
		return SenderSyncArchive.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sender sync Archives");
		}
		
		return doGetAll();
	}
	
	@GetMapping
	@RequestMapping(params = { "startDate", "endDate" })
	public Map<String, Object> searchByEventDate(@RequestParam(name = "startDate") String startDate,
	                                             @RequestParam(name = "endDate") String endDate)
	    throws ParseException {
		
		if (log.isDebugEnabled()) {
			log.debug("Fetching archived events: ");
		}
		
		Date start = null;
		if (StringUtils.isNotBlank(startDate)) {
			start = DATE_FORMAT.parse(startDate);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Start Date: " + start);
		}
		
		Date end = null;
		if (StringUtils.isNotBlank(endDate)) {
			//Roll date to end of day so that we include all archives on same day regardless of time
			LocalDateTime ldt = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DATE_PATTERN)).atTime(LocalTime.MAX);
			end = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("End Date: " + end);
		}
		
		return searchByDate(EVENT_DATE, start, end);
	}
	
}
