package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
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
	
	private ReceiverPrunedItemRepository prunedRepo;
	
	public ReceiverServiceImpl(SyncMessageRepository syncMsgRepo, SyncedMessageRepository syncedMsgRepo,
	    ReceiverSyncArchiveRepository archiveRepo, ReceiverPrunedItemRepository prunedRepo) {
		this.syncMsgRepo = syncMsgRepo;
		this.syncedMsgRepo = syncedMsgRepo;
		this.archiveRepo = archiveRepo;
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
