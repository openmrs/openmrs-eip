package org.openmrs.eip.web.sender;

import java.util.Map;

import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/sender/error")
public class SenderErrorController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(SenderErrorController.class);
	
	@Override
	public Class<?> getClazz() {
		return SenderRetryQueueItem.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sender retry items");
		}
		
		return doGetAll();
	}
	
	@GetMapping("/{id}")
	public Object get(@PathVariable("id") Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sender retry item with id: " + id);
		}
		
		return doGet(id);
	}
	
}
