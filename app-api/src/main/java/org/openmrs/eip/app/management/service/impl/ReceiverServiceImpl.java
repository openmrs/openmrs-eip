package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("receiverService")
public class ReceiverServiceImpl extends BaseService implements ReceiverService {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverServiceImpl.class);
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ReceiverPrunedItemRepository prunedRepo;
	
	public ReceiverServiceImpl(ReceiverSyncArchiveRepository archiveRepo, ReceiverPrunedItemRepository prunedRepo) {
		this.archiveRepo = archiveRepo;
		this.prunedRepo = prunedRepo;
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
