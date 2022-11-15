package org.openmrs.eip.web.sender;

import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

import java.text.ParseException;
import java.util.Map;

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
	public Map<String, Object> searchByEventDate(@RequestParam(name = PARAM_START_DATE) String startDate,
	                                             @RequestParam(name = PARAM_END_DATE) String endDate)
	    throws ParseException {
		
		if (log.isDebugEnabled()) {
			log.debug("Searching sender sync archives by start date: " + startDate + ", end date: " + endDate);
		}
		
		return doSearchByDate("eventDate", startDate, endDate);
	}
	
}
