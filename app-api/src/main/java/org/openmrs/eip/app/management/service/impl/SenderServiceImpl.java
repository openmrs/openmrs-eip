package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;
import java.util.UUID;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.openmrs.eip.app.management.repository.SenderPrunedArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.app.management.service.SenderService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
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
	
	private DebeziumEventRepository eventRepo;
	
	private SenderSyncMessageRepository syncRepo;
	
	public SenderServiceImpl(SenderSyncArchiveRepository archiveRepo, SenderPrunedArchiveRepository prunedRepo,
	    DebeziumEventRepository eventRepo, SenderSyncMessageRepository syncRepo) {
		this.archiveRepo = archiveRepo;
		this.prunedRepo = prunedRepo;
		this.eventRepo = eventRepo;
		this.syncRepo = syncRepo;
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
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void moveToSyncQueue(DebeziumEvent debeziumEvent, SyncModel syncModel) {
		if (log.isDebugEnabled()) {
			log.debug("Moving debezium event to the sync queue");
		}
		
		addToSyncQueue(debeziumEvent.getEvent(), syncModel, debeziumEvent.getDateCreated());
		
		if (log.isDebugEnabled()) {
			log.debug("Removing item from the event queue");
		}
		
		eventRepo.delete(debeziumEvent);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the event queue");
		}
	}
	
	private void addToSyncQueue(Event event, SyncModel syncModel, Date eventDate) {
		if (syncModel == null) {
			log.info("Entity not found for request with uuid: " + event.getRequestUuid());
			syncModel = SyncModel.builder().metadata(new SyncMetadata()).build();
		}
		
		final String msgUuid = UUID.randomUUID().toString();
		syncModel.getMetadata().setMessageUuid(msgUuid);
		syncModel.getMetadata().setOperation(event.getOperation());
		syncModel.getMetadata().setSnapshot(event.getSnapshot());
		syncModel.getMetadata().setRequestUuid(event.getRequestUuid());
		
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(event.getTableName());
		msg.setIdentifier(event.getIdentifier());
		msg.setOperation(event.getOperation());
		msg.setSnapshot(event.getSnapshot());
		msg.setRequestUuid(event.getRequestUuid());
		msg.setData(JsonUtils.marshall(syncModel));
		msg.setMessageUuid(msgUuid);
		msg.setDateCreated(new Date());
		msg.setEventDate(eventDate);
		
		if (log.isDebugEnabled()) {
			log.debug("Saving sync message");
		}
		
		syncRepo.save(msg);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved sync message");
		}
	}
	
}
