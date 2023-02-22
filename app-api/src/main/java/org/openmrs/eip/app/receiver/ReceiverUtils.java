package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
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
	
	public static final Set<String> CACHE_EVICT_CLASS_NAMES;
	
	public static final Set<String> INDEX_UPDATE_CLASS_NAMES;
	
	private static SyncedMessageRepository syncedMsgRepo;
	
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
	 * Generates, adds and saves {@link PostSyncAction} items for the specified {@link SyncedMessage}
	 * 
	 * @param message {@link SyncedMessage} instance
	 */
	public static void generatePostSyncActions(SyncedMessage message) {
		if (log.isDebugEnabled()) {
			log.debug("Generating post sync actions for message -> " + message);
		}
		
		Date dateCreated = new Date();
		PostSyncAction sendResp = new PostSyncAction();
		sendResp.setMessage(message);
		sendResp.setActionType(PostSyncActionType.SEND_RESPONSE);
		sendResp.setDateCreated(dateCreated);
		message.addAction(sendResp);
		
		final String modelClass = message.getModelClassName();
		if (CACHE_EVICT_CLASS_NAMES.contains(modelClass)) {
			addCacheEvictAction(message, dateCreated);
		}
		
		if (INDEX_UPDATE_CLASS_NAMES.contains(modelClass)) {
			addUpdateSearchIndexAction(message, dateCreated);
		}
		
		message.setItemized(true);
		
		if (syncedMsgRepo == null) {
			syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		}
		
		syncedMsgRepo.save(message);
		
		if (log.isDebugEnabled()) {
			log.debug("Done generating post sync actions for message");
		}
	}
	
	private static void addCacheEvictAction(SyncedMessage message, Date dateCreated) {
		addPostSyncAction(message, PostSyncActionType.CACHE_EVICT, dateCreated);
	}
	
	private static void addUpdateSearchIndexAction(SyncedMessage message, Date dateCreated) {
		addPostSyncAction(message, PostSyncActionType.SEARCH_INDEX_UPDATE, dateCreated);
	}
	
	private static void addPostSyncAction(SyncedMessage message, PostSyncActionType actionType, Date dateCreated) {
		PostSyncAction postSyncAction = new PostSyncAction();
		postSyncAction.setMessage(message);
		postSyncAction.setActionType(actionType);
		postSyncAction.setDateCreated(dateCreated);
		message.addAction(postSyncAction);
	}
	
}
