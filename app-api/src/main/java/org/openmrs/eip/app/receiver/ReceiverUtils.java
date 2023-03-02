package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
	 * Itemizes the specified {@link SyncedMessage}
	 * 
	 * @param message {@link SyncedMessage} instance
	 */
	public static void itemize(SyncedMessage message) {
		if (log.isDebugEnabled()) {
			log.debug("Itemizing message");
		}
		
		final String modelClass = message.getModelClassName();
		if (CACHE_EVICT_CLASS_NAMES.contains(modelClass)) {
			message.setCached(true);
			message.setEvictedFromCache(false);
		} else {
			message.setCached(false);
		}
		
		if (INDEX_UPDATE_CLASS_NAMES.contains(modelClass)) {
			message.setIndexed(true);
			message.setSearchIndexUpdated(false);
		} else {
			message.setIndexed(false);
		}
		
		message.setItemized(true);
		
		getSyncMsgRepo().save(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Saving itemized message");
		}
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
