package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;

import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("receiverService")
public class ReceiverServiceImpl extends BaseService implements ReceiverService {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverServiceImpl.class);
	
	private SyncMessageRepository syncMsgRepo;
	
	private SyncedMessageRepository syncedMsgRepo;
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	private ReceiverPrunedItemRepository prunedRepo;
	
	public ReceiverServiceImpl(SyncMessageRepository syncMsgRepo, SyncedMessageRepository syncedMsgRepo,
	    ReceiverSyncArchiveRepository archiveRepo, ReceiverRetryRepository retryRepo,
	    ReceiverPrunedItemRepository prunedRepo) {
		this.syncMsgRepo = syncMsgRepo;
		this.syncedMsgRepo = syncedMsgRepo;
		this.archiveRepo = archiveRepo;
		this.retryRepo = retryRepo;
		this.prunedRepo = prunedRepo;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void moveToSyncedQueue(SyncMessage message, SyncOutcome outcome) {
		SyncedMessage syncedMsg = ReceiverUtils.createSyncedMessage(message, outcome);
		if (log.isDebugEnabled()) {
			log.debug("Saving synced message");
		}
		
		syncedMsgRepo.save(syncedMsg);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved synced message, removing the sync item from the queue");
		}
		
		syncMsgRepo.delete(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed the sync item from the queue");
		}
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void archiveSyncedMessage(SyncedMessage message) {
		//TODO Check first if an archive with same message uuid does not exist yet
		log.info("Moving message to the archives queue");
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(message);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving archive");
		}
		
		archiveRepo.save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive, removing message from the synced queue");
		}
		
		syncedMsgRepo.delete(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed message removed from the synced queue");
		}
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void archiveRetry(ReceiverRetryQueueItem retry) {
		log.info("Archiving retry item with id: " + retry.getId());
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(retry);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving archive");
		}
		
		archiveRepo.save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive, removing item from the retry queue");
		}
		
		retryRepo.delete(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item removed from the retry queue");
		}
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void prune(ReceiverSyncArchive archive) {
		if (log.isDebugEnabled()) {
			log.debug("Pruning sync archive");
		}
		
		ReceiverPrunedItem pruned = new ReceiverPrunedItem(archive);
		if (log.isDebugEnabled()) {
			log.debug("Saving pruned sync item");
		}
		
		prunedRepo.save(pruned);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved pruned sync item, removing item from the archive queue");
		}
		
		archiveRepo.delete(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the archive queue");
		}
	}
	
}
