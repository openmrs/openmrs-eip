package org.openmrs.eip.web.receiver;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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

import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

@RestController
@RequestMapping(RestConstants.PATH_RECEIVER_ARCHIVE)
public class ReceiverSyncArchiveController extends BaseReceiverController {
	
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
		
		Date start = null;
		if (StringUtils.isNotBlank(startDate)) {
			start = RestConstants.DATE_FORMAT.parse(startDate);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Start date: " + start);
		}
		
		Date end = null;
		if (StringUtils.isNotBlank(endDate)) {
			//Roll date to end of day so that we include all archives on same day regardless of time
			LocalDateTime ldt = LocalDate.parse(endDate, RestConstants.DATE_FORMATTER).atTime(LocalTime.MAX);
			end = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("End date: " + end);
		}
		
		return searchByDate("dateReceived", start, end);
	}
	
	@GetMapping(params = PARAM_GRP_PROP)
	public Object getGroupedSyncMessages(@RequestParam(PARAM_GRP_PROP) String groupProperty) {
		return getGroupedItems(groupProperty);
	}
	
}
