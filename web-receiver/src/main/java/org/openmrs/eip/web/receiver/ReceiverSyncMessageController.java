package org.openmrs.eip.web.receiver;

import java.util.Map;

import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/receiver/sync")
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
	
	@GetMapping("/{id}")
	public Object get(@PathVariable("id") Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync messages with id: " + id);
		}
		
		return doGet(id);
	}
	
}
