package org.openmrs.eip.app.management.service.impl;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverArchiveService;
import org.springframework.stereotype.Service;

@Service("receiverArchiveService")
public class ReceiverArchiveServiceImpl extends BaseService implements ReceiverArchiveService {
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ReceiverPrunedItemRepository prunedRepo;
	
	@Override
	public void prune(ReceiverSyncArchive archive) {
		
	}
	
}
