package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.repository.SenderPrunedArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.app.management.service.SenderService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("senderService")
@Profile(SyncProfiles.SENDER)
public class SenderServiceImpl implements SenderService {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverServiceImpl.class);
	
	private SenderSyncArchiveRepository archiveRepo;
	
	private SenderPrunedArchiveRepository prunedRepo;
	
	public SenderServiceImpl(SenderSyncArchiveRepository archiveRepo, SenderPrunedArchiveRepository prunedRepo) {
		this.archiveRepo = archiveRepo;
		this.prunedRepo = prunedRepo;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void prune(SenderSyncArchive archive) {
		if (log.isDebugEnabled()) {
			log.debug("Pruning sync archive");
		}
		
		SenderPrunedArchive pruned = new SenderPrunedArchive(archive);
		if (log.isDebugEnabled()) {
			log.debug("Saving pruned item");
		}
		
		prunedRepo.save(pruned);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved pruned item, removing item from the archive queue");
		}
		
		archiveRepo.delete(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the archive queue");
		}
	}
	
}
