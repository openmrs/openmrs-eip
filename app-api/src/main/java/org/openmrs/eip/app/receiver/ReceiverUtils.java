package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.component.Constants.OPENMRS_DATASOURCE_NAME;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_UUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SiteSyncStatusRepository;
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
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ReceiverUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverUtils.class);
	
	private static final String PLACEHOLDER_QUERY = "[QUERY]";
	
	private static final String PLACEHOLDER_TABLE = "[TABLE]";
	
	private static final String PLACEHOLDER_COLUMN = "[COLUMN]";
	
	private static final String QUERY_PARAM_VALUE = "columnValue";
	
	private static final String QUERY_PARAM_ID = "rowId";
	
	private static final String QUERY_URI = "sql:" + PLACEHOLDER_QUERY + "?dataSource=" + OPENMRS_DATASOURCE_NAME;
	
	private static final String QUERY_URI_MGT = "sql:" + PLACEHOLDER_QUERY + "?dataSource=" + MGT_DATASOURCE_NAME;
	
	protected static final String NAME_URI = QUERY_URI.replace(PLACEHOLDER_QUERY,
	    "SELECT n.uuid FROM person_name n, person p WHERE n.person_id = p.person_id AND p.uuid = '" + PLACEHOLDER_UUID
	            + "'");
	
	protected static final String ID_URI = QUERY_URI.replace(PLACEHOLDER_QUERY,
	    "SELECT i.uuid FROM patient_identifier i, person p WHERE i.patient_id = p.person_id AND " + "p.uuid = '"
	            + PLACEHOLDER_UUID + "'");
	
	protected static final String ATTRIB_URI = QUERY_URI.replace(PLACEHOLDER_QUERY,
	    "SELECT a.uuid FROM person_attribute a, person p WHERE a.person_id = p.person_id AND p.uuid = '" + PLACEHOLDER_UUID
	            + "' AND a.person_attribute_type_id IN (SELECT person_attribute_type_id FROM person_attribute_type "
	            + "WHERE searchable = true)");
	
	protected static final String UPDATE_URI = QUERY_URI_MGT.replace(PLACEHOLDER_QUERY, "UPDATE " + PLACEHOLDER_TABLE
	        + " SET " + PLACEHOLDER_COLUMN + "=:#" + QUERY_PARAM_VALUE + " WHERE id=:#" + QUERY_PARAM_ID);
	
	private static final Set<String> CACHE_EVICT_CLASS_NAMES;
	
	private static final Set<String> INDEX_UPDATE_CLASS_NAMES;
	
	private static SyncedMessageRepository syncedMsgRepo;
	
	private static ReceiverSyncArchiveRepository archiveRepo;
	
	private static SiteSyncStatusRepository statusRepo;
	
	private static ProducerTemplate producerTemplate;
	
	private static Set<String> subclassModelClassNames;
	
	private static Map<String, String> modelClassNameParentMap;
	
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
		
		subclassModelClassNames = new HashSet();
		modelClassNameParentMap = new HashMap();
		Arrays.stream(TableToSyncEnum.values()).forEach(e -> {
			if (Utils.isSubclassTable(e.name())) {
				subclassModelClassNames.add(e.getModelClass().getName());
				
				Arrays.stream(TableToSyncEnum.values()).forEach(candidate -> {
					if (!candidate.getModelClass().equals(e.getModelClass())
					        && candidate.getModelClass().isAssignableFrom(e.getModelClass())) {
						modelClassNameParentMap.put(e.getModelClass().getName(), candidate.getModelClass().getName());
					}
				});
			}
		});
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
		syncedMessage.setDateReceived(syncMessage.getDateCreated());
		
		if (isCached(syncMessage.getModelClassName())) {
			syncedMessage.setCached(true);
		}
		
		if (isIndexed(syncMessage.getModelClassName())) {
			syncedMessage.setIndexed(true);
		}
		
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
			List<String> attribUuids = getPersonAttributeUuids(uuid);
			List<OpenmrsPayload> payloadList = new ArrayList(nameUuids.size() + idUuids.size());
			nameUuids.forEach(nameUuid -> payloadList.add(new OpenmrsPayload("person", "name", nameUuid)));
			idUuids.forEach(idUuid -> payloadList.add(new OpenmrsPayload("patient", "identifier", idUuid)));
			attribUuids.forEach(attribUuid -> payloadList.add(new OpenmrsPayload("person", "attribute", attribUuid)));
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
	
	/**
	 * Should return the uuids of the names for the person with the specified uuid
	 * 
	 * @param personUuid the person uuid
	 * @return list of person name uuids
	 */
	protected static List<String> getPersonNameUuids(String personUuid) {
		return executeQuery(NAME_URI.replace(PLACEHOLDER_UUID, personUuid));
	}
	
	/**
	 * Should return the uuids of the identifiers for the patient with the specified uuid
	 *
	 * @param patientUuid the patient uuid
	 * @return list of person name uuids
	 */
	protected static List<String> getPatientIdentifierUuids(String patientUuid) {
		return executeQuery(ID_URI.replace(PLACEHOLDER_UUID, patientUuid));
	}
	
	/**
	 * Should return the uuids of the searchable attributes for the person with the specified uuid
	 *
	 * @param personUuid the person uuid
	 * @return list of attribute name uuids
	 */
	protected static List<String> getPersonAttributeUuids(String personUuid) {
		return executeQuery(ATTRIB_URI.replace(PLACEHOLDER_UUID, personUuid));
	}
	
	private static List<String> executeQuery(String query) {
		Exchange exchange = ExchangeBuilder.anExchange(getProducerTemplate().getCamelContext()).build();
		CamelUtils.send(query, exchange);
		List<Map<String, String>> rows = exchange.getMessage().getBody(List.class);
		List<String> uuids = new ArrayList(rows.size());
		rows.forEach(r -> uuids.add(r.get("uuid")));
		
		return uuids;
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
	
	/**
	 * Checks if the specified model class name is for a subclass
	 *
	 * @param modelClassName the model class name to check
	 * @return true for a subclass otherwise false
	 */
	public static boolean isSubclass(String modelClassName) {
		return subclassModelClassNames.contains(modelClassName);
	}
	
	/**
	 * Gets the immediate parent model class name for the specified model class name
	 * 
	 * @param modelClassName the model class name
	 * @return model class name for parent model class
	 */
	public static String getParentModelClassName(String modelClassName) {
		String parent = modelClassNameParentMap.get(modelClassName);
		if (parent != null) {
			if (log.isTraceEnabled()) {
				log.trace("Parent class for " + modelClassName + " is " + parent);
			}
			
			return parent;
		}
		
		throw new EIPException("No parent class found for model class: " + modelClassName);
	}
	
	/**
	 * Updates the value of a table column to the specified value in the management database
	 * 
	 * @param tableName the table entity containing the column
	 * @param columnName the column to update
	 * @param entityId the primary key id of the row to update
	 * @param newValue the new column value to set
	 */
	public static void updateColumn(String tableName, String columnName, Long entityId, Object newValue) {
		String query = UPDATE_URI.replace(PLACEHOLDER_TABLE, tableName).replace(PLACEHOLDER_COLUMN, columnName);
		Map<String, Object> parameterValues = new HashMap(2);
		parameterValues.put(QUERY_PARAM_VALUE, newValue);
		parameterValues.put(QUERY_PARAM_ID, entityId);
		Exchange exchange = ExchangeBuilder.anExchange(getProducerTemplate().getCamelContext()).build();
		exchange.getIn().setBody(parameterValues);
		CamelUtils.send(query, exchange);
	}
	
	/**
	 * Sets and saves the last sync date for the specified site
	 * 
	 * @param site the site to update
	 * @param lastSyncDate the last sync date to set
	 */
	public static void saveLastSyncDate(SiteInfo site, Date lastSyncDate) {
		ReceiverSyncStatus status = getStatusRepo().findBySiteInfo(site);
		if (status == null) {
			status = new ReceiverSyncStatus(site, lastSyncDate);
			status.setDateCreated(new Date());
			if (log.isTraceEnabled()) {
				log.trace("Inserting initial sync status for " + site + " as " + status.getLastSyncDate());
			}
		} else {
			status.setLastSyncDate(lastSyncDate);
			if (log.isTraceEnabled()) {
				log.trace("Updating last sync date for " + site + " to " + status.getLastSyncDate());
			}
		}
		
		getStatusRepo().save(status);
		
		if (log.isTraceEnabled()) {
			log.trace("Successfully saved sync status for: " + site + " -> " + status);
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
	
	private static SiteSyncStatusRepository getStatusRepo() {
		if (statusRepo == null) {
			statusRepo = SyncContext.getBean(SiteSyncStatusRepository.class);
		}
		
		return statusRepo;
	}
	
	private static ProducerTemplate getProducerTemplate() {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		return producerTemplate;
	}
	
}
