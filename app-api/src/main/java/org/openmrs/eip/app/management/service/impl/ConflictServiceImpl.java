package org.openmrs.eip.app.management.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("conflictService")
public class ConflictServiceImpl extends BaseService implements ConflictService {
	
	private static final Logger log = LoggerFactory.getLogger(ConflictServiceImpl.class);
	
	private ConflictRepository conflictRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	private ProducerTemplate producerTemplate;
	
	private EntityServiceFacade serviceFacade;
	
	public ConflictServiceImpl(ConflictRepository conflictRepo, ReceiverRetryRepository retryRepo,
	    EntityServiceFacade serviceFacade, ProducerTemplate producerTemplate) {
		
		this.conflictRepo = conflictRepo;
		this.retryRepo = retryRepo;
		this.serviceFacade = serviceFacade;
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public List<ConflictQueueItem> getBadConflicts() {
		List<ConflictQueueItem> conflicts = conflictRepo.findByResolvedIsFalse();
		if (log.isDebugEnabled()) {
			log.debug("Conflict count: " + conflicts.size());
		}
		
		return conflicts.stream().filter(c -> {
			TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(c.getModelClassName());
			BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, c.getIdentifier());
			if (dbModel != null) {
				Class<? extends BaseHashEntity> hashClass = TableToSyncEnum.getHashClass(dbModel);
				BaseHashEntity storedHash = HashUtils.getStoredHash(c.getIdentifier(), hashClass, producerTemplate);
				return storedHash != null && HashUtils.computeHash(dbModel).equals(storedHash.getHash());
			}
			
			log.warn("No entity found in the database associated to conflict item with id: " + c.getId());
			
			return false;
		}).collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public ReceiverRetryQueueItem moveToRetryQueue(ConflictQueueItem conflict, String reason) {
		if (log.isDebugEnabled()) {
			log.debug("Moving to retry queue the conflict item with id: " + conflict.getId());
		}
		
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem(conflict);
		retry.setMessage(reason);
		if (log.isDebugEnabled()) {
			log.debug("Saving retry item");
		}
		
		retry = retryRepo.save(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved retry item, removing item from the conflict queue");
		}
		
		conflictRepo.delete(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item removed from the conflict queue");
		}
		
		return retry;
	}
	
}
