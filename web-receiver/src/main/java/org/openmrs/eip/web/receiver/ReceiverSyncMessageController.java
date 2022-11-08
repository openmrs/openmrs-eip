package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PATH_VAR;

import java.util.Map;

import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.PATH_RECEIVER_SYNC_MSG)
public class ReceiverSyncMessageController extends BaseReceiverController {
	
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
	
	@GetMapping("/{" + PATH_VAR + "}")
	public Object get(@PathVariable(PATH_VAR) Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching receiver sync messages with id: " + id);
		}
		
		return doGet(id);
	}
	
	@GetMapping(params = PARAM_GRP_PROP)
	public Object getGroupedSyncMessages(@RequestParam(PARAM_GRP_PROP) String groupProperty) {
		return getGroupedItems(groupProperty);
	}
	
}
