package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.repository.PatientIdentifierRepository;
import org.openmrs.eip.component.repository.PersonAttributeRepository;
import org.openmrs.eip.component.repository.PersonNameRepository;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ReceiverUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverUtils.class);
	
	private static final String PLACEHOLDER_TABLE = "[TABLE]";
	
	private static final String PLACEHOLDER_COLUMN = "[COLUMN]";
	
	private static final String UPDATE_URI = "UPDATE " + PLACEHOLDER_TABLE + " SET " + PLACEHOLDER_COLUMN
	        + " = ? WHERE id = ?";
	
	private static final Set<String> CACHE_EVICT_CLASS_NAMES;
	
	private static final Set<String> INDEX_UPDATE_CLASS_NAMES;
	
	private static PersonNameRepository nameRepo;
	
	private static PatientIdentifierRepository idRepo;
	
	private static PersonAttributeRepository attribRepo;
	
	private static Set<String> subclassModelClassNames;
	
	private static Map<String, String> modelClassNameParentMap;
	
	private static ReceiverActiveMqMessagePublisher activeMqPublisher;
	
	private static DataSource mgtDataSource;
	
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
	 * @param syncMessage {@link SyncMessage} object
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
	 * Creates a {@link SyncedMessage} for the specified {@link ConflictQueueItem}
	 *
	 * @param conflict the conflict item
	 * @return synced message
	 */
	public static SyncedMessage createSyncedMessage(ConflictQueueItem conflict) {
		SyncedMessage syncedMessage = new SyncedMessage(SyncOutcome.SUCCESS);
		BeanUtils.copyProperties(conflict, syncedMessage, "id", "dateCreated");
		syncedMessage.setDateCreated(new Date());
		syncedMessage.setResponseSent(true);
		
		if (isCached(conflict.getModelClassName())) {
			syncedMessage.setCached(true);
		}
		
		if (isIndexed(conflict.getModelClassName())) {
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
			List<String> nameUuids = getNameRepo().getPersonNameUuids(uuid);
			List<String> idUuids = getIdRepo().getPatientIdentifierUuids(uuid);
			List<String> attribUuids = getAttribRepo().getPersonAttributeUuids(uuid);
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
		try (Connection c = getMgtDataSource().getConnection(); PreparedStatement s = c.prepareStatement(query)) {
			s.setObject(1, newValue);
			s.setLong(2, entityId);
			s.executeUpdate();
		}
		catch (Throwable t) {
			throw new EIPException("An error occurred while updating database column value", t);
		}
	}
	
	public static String getSiteQueueName(String siteIdentifier) {
		String endpoint = getActiveMqMessagePublisher().getCamelOutputEndpoint(siteIdentifier);
		if (!endpoint.startsWith("activemq:")) {
			throw new EIPException(endpoint + " is an invalid message broker endpoint value for outbound messages");
		}
		
		return endpoint.substring(endpoint.indexOf(":") + 1);
	}
	
	private static PersonNameRepository getNameRepo() {
		if (nameRepo == null) {
			nameRepo = SyncContext.getBean(PersonNameRepository.class);
		}
		
		return nameRepo;
	}
	
	private static PatientIdentifierRepository getIdRepo() {
		if (idRepo == null) {
			idRepo = SyncContext.getBean(PatientIdentifierRepository.class);
		}
		
		return idRepo;
	}
	
	private static PersonAttributeRepository getAttribRepo() {
		if (attribRepo == null) {
			attribRepo = SyncContext.getBean(PersonAttributeRepository.class);
		}
		
		return attribRepo;
	}
	
	private static ReceiverActiveMqMessagePublisher getActiveMqMessagePublisher() {
		if (activeMqPublisher == null) {
			activeMqPublisher = SyncContext.getBean(ReceiverActiveMqMessagePublisher.class);
		}
		
		return activeMqPublisher;
	}
	
	private static DataSource getMgtDataSource() {
		if (mgtDataSource == null) {
			mgtDataSource = SyncContext.getBean(MGT_DATASOURCE_NAME);
		}
		
		return mgtDataSource;
	}
	
}
