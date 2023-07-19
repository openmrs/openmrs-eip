package org.openmrs.eip.web.receiver;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.receiver.ConflictVerifyingTask;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/receiver/conflict")
public class ConflictController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ConflictController.class);
	
	private ConflictService service;
	
	public ConflictController(ConflictService service) {
		this.service = service;
	}
	
	@Override
	public Class<?> getClazz() {
		return ConflictQueueItem.class;
	}
	
	@GetMapping
	public Map<String, Object> getAll() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching conflicts");
		}
		
		Map<String, Object> results = new HashMap(2);
		Integer count = on(camelContext).to("jpa:" + getName() + "?query=SELECT count(*) FROM " + getName())
		        .request(Integer.class);
		
		results.put(FIELD_COUNT, count);
		
		List<Object> items;
		if (count > 0) {
			items = on(camelContext)
			        .to("jpa:" + getName() + "?query=SELECT c FROM " + getName() + " c &maximumResults=" + DEFAULT_MAX_COUNT)
			        .request(List.class);
			
			results.put(FIELD_ITEMS, items);
		} else {
			results.put(FIELD_ITEMS, Collections.emptyList());
		}
		
		return results;
	}
	
	@GetMapping("/{id}")
	public Object get(@PathVariable("id") Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Fetching conflict with id: " + id);
		}
		
		return doGet(id);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Removing conflict with id: " + id);
		}
		
		service.moveToArchiveQueue((ConflictQueueItem) doGet(id));
	}
	
	@PostMapping("/verify/start")
	public void startVerifyTask() {
		if (log.isDebugEnabled()) {
			log.debug("Processing request to start " + ConflictVerifyingTask.getInstance().getTaskName());
		}
		
		ExecutorService executor = SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR);
		executor.execute(ConflictVerifyingTask.getInstance());
	}
	
	@GetMapping("/verify/status")
	public boolean getVerifyTaskStatus() {
		if (log.isDebugEnabled()) {
			log.debug("Getting status of " + ConflictVerifyingTask.getInstance().getTaskName());
		}
		
		return ConflictVerifyingTask.getInstance().isStarted();
	}
	
}
