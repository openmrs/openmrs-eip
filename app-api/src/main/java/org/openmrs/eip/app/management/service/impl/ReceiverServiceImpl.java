package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("receiverService")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverServiceImpl extends BaseService implements ReceiverService {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverServiceImpl.class);
	
	private SyncMessageRepository syncMsgRepo;
	
	private SyncedMessageRepository syncedMsgRepo;
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	private ConflictRepository conflictRepo;
	
	private ReceiverPrunedItemRepository prunedRepo;
	
	private EntityServiceFacade serviceFacade;
	
	private ProducerTemplate producerTemplate;
	
	public ReceiverServiceImpl(SyncMessageRepository syncMsgRepo, SyncedMessageRepository syncedMsgRepo,
	    ReceiverSyncArchiveRepository archiveRepo, ReceiverRetryRepository retryRepo, ConflictRepository conflictRepo,
	    ReceiverPrunedItemRepository prunedRepo, EntityServiceFacade serviceFacade, ProducerTemplate producerTemplate) {
		this.syncMsgRepo = syncMsgRepo;
		this.syncedMsgRepo = syncedMsgRepo;
		this.archiveRepo = archiveRepo;
		this.retryRepo = retryRepo;
		this.conflictRepo = conflictRepo;
		this.prunedRepo = prunedRepo;
		this.serviceFacade = serviceFacade;
		this.producerTemplate = producerTemplate;
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
			log.debug("Successfully saved archive, removing item from the synced queue");
		}
		
		syncedMsgRepo.delete(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the synced queue");
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
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void updateHash(String modelClassname, String identifier) {
		if (log.isDebugEnabled()) {
			log.debug("Updating entity hash to match the current state in the database");
		}
		
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(modelClassname);
		BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, identifier);
		BaseHashEntity storedHash = HashUtils.getStoredHash(identifier, tableToSyncEnum.getHashClass(), producerTemplate);
		//TODO If hash does not exist, Should we insert one?
		storedHash.setHash(HashUtils.computeHash(dbModel));
		storedHash.setDateChanged(LocalDateTime.now());
		HashUtils.saveHash(storedHash, producerTemplate, false);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved new hash for the entity");
		}
	}
	
	@Override
	public boolean hasSyncItem(String identifier, String modelClassname) {
		List<String> classNames = Utils.getListOfModelClassHierarchy(modelClassname);
		return syncMsgRepo.countByIdentifierAndModelClassNameIn(identifier, classNames) > 0;
	}
	
	@Override
	public boolean hasRetryItem(String identifier, String modelClassname) {
		List<String> classNames = Utils.getListOfModelClassHierarchy(modelClassname);
		return retryRepo.countByIdentifierAndModelClassNameIn(identifier, classNames) > 0;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void processFailedSyncItem(SyncMessage message, String exceptionType, String errorMsg) {
		log.info("Adding item to retry queue");
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem(message, exceptionType, errorMsg);
		if (log.isDebugEnabled()) {
			log.debug("Saving retry item");
		}
		
		retryRepo.save(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved retry item");
		}
		
		moveToSyncedQueue(message, SyncOutcome.ERROR);
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void processConflictedSyncItem(SyncMessage message) {
		log.info("Adding item to conflict queue");
		ConflictQueueItem conflict = new ConflictQueueItem(message);
		if (log.isDebugEnabled()) {
			log.debug("Saving conflict item");
		}
		
		conflictRepo.save(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved conflict item");
		}
		
		moveToSyncedQueue(message, SyncOutcome.CONFLICT);
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void moveToConflictQueue(ReceiverRetryQueueItem retry) {
		log.info("Moving to conflict queue the retry item with uuid: " + retry.getMessageUuid());
		
		ConflictQueueItem conflict = new ConflictQueueItem(retry);
		if (log.isDebugEnabled()) {
			log.debug("Saving conflict item");
		}
		
		conflictRepo.save(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved conflict item, removing the retry item from the queue");
		}
		
		retryRepo.delete(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed the retry item from the queue");
		}
	}
	
}
