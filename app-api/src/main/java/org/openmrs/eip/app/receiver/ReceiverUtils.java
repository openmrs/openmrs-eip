package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public final class ReceiverUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverUtils.class);
	
	private static final Set<String> CACHE_EVICT_CLASS_NAMES;
	
	private static final Set<String> INDEX_UPDATE_CLASS_NAMES;
	
	private static SyncedMessageRepository syncedMsgRepo;
	
	private static ReceiverSyncArchiveRepository archiveRepo;
	
	static {
		//TODO instead define the cache and search index requirements on the TableToSyncEnum
		CACHE_EVICT_CLASS_NAMES = new HashSet();
		CACHE_EVICT_CLASS_NAMES.add(PersonModel.class.getName());
		CACHE_EVICT_CLASS_NAMES.add(PersonNameModel.class.getName());
		CACHE_EVICT_CLASS_NAMES.add(PersonAddressModel.class.getName());
		CACHE_EVICT_CLASS_NAMES.add(PersonAttributeModel.class.getName());
		CACHE_EVICT_CLASS_NAMES.add(UserModel.class.getName());
		//Patient extends Person, so we need to evict linked person records
		CACHE_EVICT_CLASS_NAMES.add(PatientModel.class.getName());
		
		INDEX_UPDATE_CLASS_NAMES = new HashSet();
		INDEX_UPDATE_CLASS_NAMES.add(PersonNameModel.class.getName());
		INDEX_UPDATE_CLASS_NAMES.add(PersonAttributeModel.class.getName());
		INDEX_UPDATE_CLASS_NAMES.add(PatientIdentifierModel.class.getName());
		//We need to update the search index for the associated person names
		INDEX_UPDATE_CLASS_NAMES.add(PersonModel.class.getName());
		//We need to update the  search index for the associated patient identifiers
		INDEX_UPDATE_CLASS_NAMES.add(PatientModel.class.getName());
	}
	
	/**
	 * Creates a {@link SyncedMessage} for the specified {@link SyncMessage}, if the associated entity
	 * is neither cached nor indexed this method returns null.
	 *
	 * @param syncMessage {@link org.openmrs.eip.app.management.entity.SyncMessage} object
	 * @return synced message or null
	 */
	public static SyncedMessage createSyncedMessage(SyncMessage syncMessage) {
		SyncedMessage syncedMsg = createSyncedMessage(syncMessage, syncMessage.getModelClassName());
		syncedMsg.setDateReceived(syncMessage.getDateCreated());
		
		return syncedMsg;
	}
	
	/**
	 * Creates a {@link SyncedMessage} for the specified {@link ReceiverRetryQueueItem}, if the
	 * associated entity is neither cached nor indexed this method returns null.
	 *
	 * @param retry {@link ReceiverRetryQueueItem} object
	 * @return synced message or null
	 */
	public static SyncedMessage createSyncedMessageFromRetry(ReceiverRetryQueueItem retry) {
		return createSyncedMessage(retry, retry.getModelClassName());
	}
	
	/**
	 * Creates a {@link SyncedMessage} for the specified {@link ConflictQueueItem}, if the associated
	 * entity is neither cached nor indexed this method returns null.
	 *
	 * @param conflict {@link ConflictQueueItem} object
	 * @return synced message or null
	 */
	public static SyncedMessage createSyncedMessageFromConflict(ConflictQueueItem conflict) {
		return createSyncedMessage(conflict, conflict.getModelClassName());
	}
	
	/**
	 * Moves the specified {@link SyncedMessage} to the archives queue if all the post sync actions have
	 * been successfully processed
	 * 
	 * @param message the message to archive
	 */
	public static void archiveMessage(SyncedMessage message) {
		//TODO Check first if an archive with same message uuid does not exist yet
		log.info("Moving message to the archives queue");
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(message);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving archive");
		}
		
		getArchiveRepo().save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive, removing message from the synced queue");
		}
		
		getSyncMsgRepo().delete(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed message removed from the synced queue");
		}
	}
	
	private static SyncedMessage createSyncedMessage(Object source, String modelClass) {
		SyncedMessage syncedMessage = new SyncedMessage();
		BeanUtils.copyProperties(source, syncedMessage, "id", "dateCreated");
		syncedMessage.setDateCreated(new Date());
		
		if (CACHE_EVICT_CLASS_NAMES.contains(modelClass)) {
			syncedMessage.setCached(true);
		}
		
		if (INDEX_UPDATE_CLASS_NAMES.contains(modelClass)) {
			syncedMessage.setIndexed(true);
		}
		
		return syncedMessage;
	}
	
	private static SyncedMessageRepository getSyncMsgRepo() {
		if (syncedMsgRepo == null) {
			syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		}
		
		return syncedMsgRepo;
	}
	
	private static ReceiverSyncArchiveRepository getArchiveRepo() {
		if (archiveRepo == null) {
			archiveRepo = SyncContext.getBean(ReceiverSyncArchiveRepository.class);
		}
		
		return archiveRepo;
	}
	
}
