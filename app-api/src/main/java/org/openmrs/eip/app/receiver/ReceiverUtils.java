package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.component.Constants.OPENMRS_DATASOURCE_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
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

import com.fasterxml.jackson.core.JsonProcessingException;

public final class ReceiverUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverUtils.class);
	
	private static final Set<String> CACHE_EVICT_CLASS_NAMES;
	
	private static final Set<String> INDEX_UPDATE_CLASS_NAMES;
	
	private static SyncedMessageRepository syncedMsgRepo;
	
	private static ReceiverSyncArchiveRepository archiveRepo;
	
	private static ProducerTemplate producerTemplate;
	
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
	 * Checks whether an entity of the specified model class name is cached
	 * 
	 * @param modelClass the model classname to check
	 * @return true if entities of the model class are cached otherwise false
	 */
	public static boolean isCached(String modelClass) {
		return CACHE_EVICT_CLASS_NAMES.contains(modelClass);
	}
	
	/**
	 * Checks whether an entity of the specified model class name is indexed
	 *
	 * @param modelClass the model classname to check
	 * @return true if entities of the model class are indexed otherwise false
	 */
	public static boolean isIndexed(String modelClass) {
		return INDEX_UPDATE_CLASS_NAMES.contains(modelClass);
	}
	
	/**
	 * Creates a {@link SyncedMessage} for the specified {@link SyncMessage}.
	 *
	 * @param syncMessage {@link org.openmrs.eip.app.management.entity.SyncMessage} object
	 * @param outcome {@link SyncOutcome}
	 * @return synced message
	 */
	public static SyncedMessage createSyncedMessage(SyncMessage syncMessage, SyncOutcome outcome) {
		SyncedMessage syncedMessage = new SyncedMessage(outcome);
		BeanUtils.copyProperties(syncMessage, syncedMessage, "id", "dateCreated");
		syncedMessage.setDateCreated(new Date());
		
		if (isCached(syncMessage.getModelClassName())) {
			syncedMessage.setCached(true);
		}
		
		if (isIndexed(syncMessage.getModelClassName())) {
			syncedMessage.setIndexed(true);
		}
		
		syncedMessage.setDateReceived(syncMessage.getDateCreated());
		
		return syncedMessage;
	}
	
	/**
	 * Generate the cache eviction {@link OpenmrsPayload} for the entity matching the modelClass and
	 * identifier
	 * 
	 * @param modelClass the model classname for the entity
	 * @param identifier the entity identifier
	 * @param operation sync operation
	 * @return openmrs cache eviction payload
	 */
	public static Object generateEvictionPayload(String modelClass, String identifier, SyncOperation operation) {
		String uuid = null;
		//Users are not deleted, so no need to clear the cache for all users as we do for other cached entities
		if (SyncOperation.d != operation || UserModel.class.getName().equals(modelClass)) {
			uuid = identifier;
		}
		
		String resource;
		String subResource = null;
		if (PersonNameModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "name";
		} else if (PersonAttributeModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "attribute";
		} else if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			resource = "person";
		} else if (PersonAddressModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "address";
		} else if (UserModel.class.getName().equals(modelClass)) {
			//TODO Remove this clause when user and provider sync is stopped
			resource = "user";
		} else {
			throw new EIPException("Don't know how to handle cache eviction for entity of type: " + modelClass);
		}
		
		try {
			return ReceiverConstants.MAPPER.writeValueAsString(new OpenmrsPayload(resource, subResource, uuid));
		}
		catch (JsonProcessingException e) {
			throw new EIPException("Failed to generate cache evict payload", e);
		}
	}
	
	/**
	 * Generate the search index update {@link OpenmrsPayload} for the entity matching the modelClass
	 * and identifier, note that this method can also return multiple payloads
	 *
	 * @param modelClass the model classname for the entity
	 * @param identifier the entity identifier
	 * @param operation sync operation
	 * @return openmrs search index payload(s)
	 */
	public static Object generateSearchIndexUpdatePayload(String modelClass, String identifier, SyncOperation operation) {
		String uuid = null;
		if (SyncOperation.d != operation || PersonModel.class.getName().equals(modelClass)
		        || PatientModel.class.getName().equals(modelClass)) {
			
			uuid = identifier;
		}
		
		Object payload;
		if (PersonNameModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("person", "name", uuid);
		} else if (PatientIdentifierModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("patient", "identifier", uuid);
		} else if (PersonAttributeModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("person", "attribute", uuid);
		} else if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			List<String> nameUuids = getPersonNameUuids(uuid);
			List<String> idUuids = getPatientIdentifierUuids(uuid);
			List<OpenmrsPayload> payloadList = new ArrayList(nameUuids.size() + idUuids.size());
			nameUuids.forEach(nameUuid -> payloadList.add(new OpenmrsPayload("person", "name", nameUuid)));
			idUuids.forEach(idUuid -> payloadList.add(new OpenmrsPayload("patient", "identifier", idUuid)));
			payload = payloadList;
		} else {
			throw new EIPException("Don't know how to handle search index update for entity of type: " + modelClass);
		}
		
		try {
			if (!Collection.class.isAssignableFrom(payload.getClass())) {
				return ReceiverConstants.MAPPER.writeValueAsString(payload);
			}
			
			Collection payLoadColl = (Collection) payload;
			List<String> payloads = new ArrayList(payLoadColl.size());
			for (Object pl : payLoadColl) {
				payloads.add(ReceiverConstants.MAPPER.writeValueAsString(pl));
			}
			
			return payloads;
		}
		catch (JsonProcessingException e) {
			throw new EIPException("Failed to generate search index update payload", e);
		}
	}
	
	protected static List<String> getPersonNameUuids(String personUuid) {
		String q = "SELECT n.uuid FROM person p, person_name n WHERE p.person_id = n.person_id AND p.uuid = '" + personUuid
		        + "'";
		
		return executeQuery(q);
	}
	
	protected static List<String> getPatientIdentifierUuids(String patientUuid) {
		String q = "SELECT i.uuid FROM person p, patient_identifier i WHERE p.person_id = i.patient_id AND " + "p.uuid = '"
		        + patientUuid + "'";
		
		return executeQuery(q);
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
	
	private static List<String> executeQuery(String query) {
		Exchange exchange = ExchangeBuilder.anExchange(getProducerTemplate().getCamelContext()).build();
		CamelUtils.send("sql:" + query + "?dataSource=" + OPENMRS_DATASOURCE_NAME, exchange);
		List<Map<String, String>> rows = exchange.getMessage().getBody(List.class);
		List<String> uuids = new ArrayList(rows.size());
		rows.forEach(r -> uuids.add(r.get("uuid")));
		
		return uuids;
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
	
	private static ProducerTemplate getProducerTemplate() {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		return producerTemplate;
	}
	
}
