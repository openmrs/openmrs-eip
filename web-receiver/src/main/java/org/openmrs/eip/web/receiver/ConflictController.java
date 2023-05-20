package org.openmrs.eip.web.receiver;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_CLASS;
import static org.openmrs.eip.component.Constants.QUERY_SAVE_HASH;
import static org.openmrs.eip.web.RestConstants.DEFAULT_MAX_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_COUNT;
import static org.openmrs.eip.web.RestConstants.FIELD_ITEMS;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.BaseRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/receiver/conflict")
public class ConflictController extends BaseRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ConflictController.class);
	
	@Autowired
	private EntityServiceFacade entityServiceFacade;
	
	@Autowired
	private ConflictService service;
	
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
		Integer count = on(camelContext)
		        .to("jpa:" + getName() + "?query=SELECT count(*) FROM " + getName() + " WHERE resolved = false")
		        .request(Integer.class);
		
		results.put(FIELD_COUNT, count);
		
		List<Object> items;
		if (count > 0) {
			items = on(camelContext).to("jpa:" + getName() + "?query=SELECT c FROM " + getName()
			        + " c WHERE c.resolved = false &maximumResults=" + DEFAULT_MAX_COUNT).request(List.class);
			
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
	
	@PatchMapping("/{id}")
	public Object update(@RequestBody Map<String, Object> payload, @PathVariable("id") Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Updating conflict with id: " + id);
		}
		
		ConflictQueueItem conflict = (ConflictQueueItem) doGet(id);
		
		log.info("Archiving the conflict item");
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(conflict);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving sync archive");
		}
		
		producerTemplate.sendBody("jpa:" + ReceiverSyncArchive.class.getSimpleName(), archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved sync archive");
		}
		
		//Currently the only update allowed is marking the conflict as resolved
		conflict.setResolved(Boolean.valueOf(payload.get("resolved").toString()));
		
		conflict = producerTemplate.requestBody("jpa:" + getName(), conflict, ConflictQueueItem.class);
		
		log.info("Updating entity hash to match the current state in the receiver database");
		
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(conflict.getModelClassName());
		BaseModel dbModel = entityServiceFacade.getModel(tableToSyncEnum, conflict.getIdentifier());
		BaseHashEntity storedHash = HashUtils.getStoredHash(conflict.getIdentifier(), tableToSyncEnum.getHashClass(),
		    producerTemplate);
		storedHash.setHash(HashUtils.computeHash(dbModel));
		storedHash.setDateChanged(LocalDateTime.now());
		producerTemplate.sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, tableToSyncEnum.getHashClass().getSimpleName()),
		    storedHash);
		
		log.info("Successfully saved new hash for the entity");
		
		return conflict;
	}
	
	@GetMapping("/verify")
	public int verify() {
		if (log.isDebugEnabled()) {
			log.debug("Getting count conflicts where the entity hashes are valid");
		}
		
		return service.getBadConflicts().size();
	}
	
	@PostMapping("/clean")
	public int clean() {
		if (log.isDebugEnabled()) {
			log.debug("Cleaning conflicts");
		}
		
		List<ConflictQueueItem> conflicts = service.getBadConflicts();
		
		log.info("Moving " + conflicts.size() + " conflict(s) where the entity hashes are valid");
		
		for (ConflictQueueItem c : conflicts) {
			service.moveToRetryQueue(c, "Moved from conflict queue because the hash on file is valid");
		}
		
		return conflicts.size();
	}
	
}
