package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
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
import org.springframework.transaction.annotation.Transactional;

@Service("conflictService")
public class ConflictServiceImpl extends BaseService implements ConflictService {
	
	private static final Logger log = LoggerFactory.getLogger(ConflictServiceImpl.class);
	
	private ConflictRepository conflictRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ProducerTemplate producerTemplate;
	
	private EntityServiceFacade serviceFacade;
	
	public ConflictServiceImpl(ConflictRepository conflictRepo, ReceiverRetryRepository retryRepo,
	    ReceiverSyncArchiveRepository archiveRepo, EntityServiceFacade serviceFacade, ProducerTemplate producerTemplate) {
		this.conflictRepo = conflictRepo;
		this.retryRepo = retryRepo;
		this.archiveRepo = archiveRepo;
		this.serviceFacade = serviceFacade;
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
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
			log.debug("Successfully removed item from the conflict queue");
		}
		
		return retry;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public ReceiverSyncArchive moveToArchiveQueue(ConflictQueueItem conflict) {
		if (log.isDebugEnabled()) {
			log.debug("Moving to archive queue the conflict item with id: " + conflict.getId());
		}
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(conflict);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving archive item");
		}
		
		archive = archiveRepo.save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive item, updating entity hash to match the current state in the database");
		}
		
		String uuid = conflict.getIdentifier();
		String modelClassname = conflict.getModelClassName();
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(modelClassname);
		BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, uuid);
		BaseHashEntity storedHash = HashUtils.getStoredHash(uuid, tableToSyncEnum.getHashClass(), producerTemplate);
		storedHash.setHash(HashUtils.computeHash(dbModel));
		storedHash.setDateChanged(LocalDateTime.now());
		HashUtils.saveHash(storedHash, producerTemplate, false);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved new hash for the entity, removing item from the conflict queue");
		}
		
		conflictRepo.delete(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the conflict queue");
		}
		
		return archive;
	}
	
	@Override
	public void resolveWithDatabaseState(ConflictQueueItem conflict) {
		if (log.isDebugEnabled()) {
			log.info("Resolving conflict with database state as the winner");
		}
		
		moveToArchiveQueue(conflict);
	}
	
	@Override
	public void resolveWithRemoteState(ConflictQueueItem conflict) {
		
	}
	
	@Override
	public void resolveWithMerge(ConflictQueueItem conflict, Object mergedState) {
		
	}
	
}
