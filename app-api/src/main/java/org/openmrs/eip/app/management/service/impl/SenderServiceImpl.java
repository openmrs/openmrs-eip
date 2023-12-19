package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.openmrs.eip.app.management.repository.SenderPrunedArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderRetryRepository;
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
	
	private static final Logger log = LoggerFactory.getLogger(SenderServiceImpl.class);
	
	private SenderSyncArchiveRepository archiveRepo;
	
	private SenderPrunedArchiveRepository prunedRepo;
	
	private DebeziumEventRepository eventRepo;
	
	private SenderSyncMessageRepository syncRepo;
	
	private SenderRetryRepository retryRepo;
	
	public SenderServiceImpl(SenderSyncArchiveRepository archiveRepo, SenderPrunedArchiveRepository prunedRepo,
	    DebeziumEventRepository eventRepo, SenderSyncMessageRepository syncRepo, SenderRetryRepository retryRepo) {
		this.archiveRepo = archiveRepo;
		this.prunedRepo = prunedRepo;
		this.eventRepo = eventRepo;
		this.syncRepo = syncRepo;
		this.retryRepo = retryRepo;
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
	public void moveEventToSyncQueue(DebeziumEvent debeziumEvent, SyncModel syncModel) {
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
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void moveRetryToSyncQueue(SenderRetryQueueItem retry, SyncModel syncModel) {
		if (log.isDebugEnabled()) {
			log.debug("Moving retry item to the sync queue");
		}
		
		addToSyncQueue(retry.getEvent(), syncModel, retry.getEventDate());
		
		if (log.isDebugEnabled()) {
			log.debug("Removing from the retry queue an item with id: " + retry.getId());
		}
		
		retryRepo.delete(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed from the retry queue an item with id: " + retry.getId());
		}
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void moveToRetryQueue(DebeziumEvent debeziumEvent, String exceptionType, String errorMessage) {
		log.info("Moving event to the retry queue");
		
		SenderRetryQueueItem retry = new SenderRetryQueueItem();
		retry.setEvent(debeziumEvent.getEvent());
		retry.setExceptionType(exceptionType);
		retry.setMessage(errorMessage);
		retry.setEventDate(debeziumEvent.getDateCreated());
		retry.setDateCreated(new Date());
		
		if (log.isDebugEnabled()) {
			log.debug("Saving retry item");
		}
		
		retryRepo.save(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved retry item");
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Removing item from the event queue");
		}
		
		eventRepo.delete(debeziumEvent);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the event queue");
		}
	}
	
	private void addToSyncQueue(Event event, SyncModel syncModel, Date eventDate) {
		if (syncModel == null && "r".equals(event.getOperation())) {
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
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void archiveSyncMessage(SenderSyncMessage message, LocalDateTime dateReceivedByReceiver) {
		log.info("Archiving the sync item");
		
		SenderSyncArchive archive = new SenderSyncArchive(message);
		archive.setDateCreated(new Date());
		archive.setDateReceivedByReceiver(dateReceivedByReceiver);
		if (log.isDebugEnabled()) {
			log.debug("Saving archive");
		}
		
		archiveRepo.save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive, removing sync item(s) with a matching message uuid");
		}
		
		syncRepo.deleteByMessageUuid(message.getMessageUuid());
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed sync item(s) from the sync queue with a matching message uuid");
		}
	}
	
}
