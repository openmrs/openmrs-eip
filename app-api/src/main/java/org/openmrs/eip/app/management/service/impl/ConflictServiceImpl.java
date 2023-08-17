package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution;
import org.openmrs.eip.component.exception.EIPException;
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
	
	private ReceiverService receiverService;
	
	public ConflictServiceImpl(ConflictRepository conflictRepo, ReceiverRetryRepository retryRepo,
	    ReceiverSyncArchiveRepository archiveRepo, ReceiverService receiverService) {
		this.conflictRepo = conflictRepo;
		this.retryRepo = retryRepo;
		this.archiveRepo = archiveRepo;
		this.receiverService = receiverService;
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
			log.debug("Successfully saved archive item");
		}
		
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		
		if (log.isDebugEnabled()) {
			log.debug("Removing item from the conflict queue");
		}
		
		conflictRepo.delete(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the conflict queue");
		}
		
		return archive;
	}
	
	@Override
	public void resolve(ConflictResolution resolution) {
		if (resolution.getConflict() == null) {
			throw new EIPException("");
		} else if (resolution.getDecision() == null) {
			throw new EIPException("");
		}
		
		log.info("Resolving conflict with decision: " + resolution.getDecision());
		
		//TODO should we track the conflict resolution log?
		
		switch (resolution.getDecision()) {
			case IGNORE_NEW:
				moveToArchiveQueue(resolution.getConflict());
				break;
			case SYNC_NEW:
				resolveWithNewState(resolution);
				break;
			case MERGE:
				resolveAsMerge(resolution);
				break;
		}
	}
	
	private void resolveWithNewState(ConflictResolution resolution) {
		ConflictQueueItem conflict = resolution.getConflict();
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		
		moveToRetryQueue(conflict, "Moved from conflict queue after conflict resolution");
	}
	
	private void resolveAsMerge(ConflictResolution resolution) {
		
	}
	
}
