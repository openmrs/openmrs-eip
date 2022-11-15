package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;

import java.text.ParseException;
import java.util.Map;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		
		return doSearchByDate("dateReceived", startDate, endDate);
	}
	
	@GetMapping(params = PARAM_GRP_PROP)
	public Object getGroupedSyncMessages(@RequestParam(PARAM_GRP_PROP) String groupProperty) {
		return getGroupedItems(groupProperty);
	}
	
}
