package org.openmrs.eip;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(HashUtils.class);
	
	private static Map<Class<? extends BaseModel>, Set<String>> modelClassDatetimePropsMap = null;
	
	private static ReceiverService receiverService;
	
	/**
	 * Computes the hash of the specified model, the logic is such that it removes null values, extracts
	 * uuids for all light entity fields, converts datetime fields to milliseconds since the epoch,
	 * sorts the values by field names, Stringifies the values, trims the values and concatenates all
	 * values into a single string which is then hashed, this implementation has implications below,
	 *
	 * <pre>
	 * 	  - It is case sensitive
	 * 	  - Field value changes from null to an empty string or whitespace characters and vice versa are ignored
	 * </pre>
	 *
	 * @param model the BaseModel object
	 * @return md5 hash
	 */
	public static String computeHash(BaseModel model) {
		String payload = JsonUtils.marshall(model);
		Map<String, Object> data = JsonUtils.unmarshal(payload, Map.class);
		data = data.entrySet().stream().filter(e -> e.getValue() != null)
		        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
			        if (entry.getKey().endsWith("Uuid")) {
				        return ModelUtils.decomposeUuid(entry.getValue().toString()).get().getUuid();
			        }
			        
			        if (getDatetimePropertyNames(model.getClass()).contains(entry.getKey())) {
				        try {
					        Object date = PropertyUtils.getProperty(model, entry.getKey());
					        return ((LocalDateTime) date).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
				        }
				        catch (Exception e) {
					        throw new RuntimeException(
					                "Failed to normalize datetime field " + model.getClass() + "." + entry.getKey(), e);
				        }
			        }
			        
			        return entry.getValue();
		        }));
		
		Map<String, Object> treeMap = new TreeMap(data);
		List<Object> values = new ArrayList(treeMap.size());
		for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
			values.add(entry.getValue());
		}
		
		String val = values.stream().map(o -> o.toString().trim()).collect(Collectors.joining());
		
		return DigestUtils.md5Hex(val.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Gets a set of property names of the specified model class object that are of type LocalDateTime
	 *
	 * @param modelClass the model class object to inspect
	 * @return a set of property names
	 */
	public static Set<String> getDatetimePropertyNames(Class<? extends BaseModel> modelClass) {
		synchronized (HashUtils.class) {
			if (modelClassDatetimePropsMap == null) {
				modelClassDatetimePropsMap = new HashMap();
				Arrays.stream(TableToSyncEnum.values()).forEach(e -> {
					Set<String> datetimeProps = new HashSet();
					Arrays.stream(PropertyUtils.getPropertyDescriptors(e.getModelClass())).forEach(d -> {
						if (LocalDateTime.class.equals(d.getPropertyType())) {
							datetimeProps.add(d.getName());
						}
					});
					
					modelClassDatetimePropsMap.put(e.getModelClass(), datetimeProps);
				});
			}
		}
		
		return modelClassDatetimePropsMap.get(modelClass);
	}
	
	/**
	 * Creates a new instance of the specified hash entity class
	 *
	 * @param hashClass the hash entity class to instantiate
	 * @return an instance of the hash entity class
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static BaseHashEntity instantiateHashEntity(Class<? extends BaseHashEntity> hashClass)
	    throws IllegalAccessException, InstantiationException {
		
		return hashClass.newInstance();
	}
	
	/**
	 * Looks up the stored hash for the entity with the specified identifier from the management DB
	 *
	 * @param identifier the unique identifier of the entity
	 * @param hashClass entity hash class
	 * @return the saved hash entity object otherwise null
	 */
	public static BaseHashEntity getStoredHash(String identifier, Class<? extends BaseHashEntity> hashClass) {
		return getReceiverService().getHash(identifier, hashClass);
	}
	
	/**
	 * Saves the specified hash object to the database
	 *
	 * @param object hash object to save
	 * @param handleDuplicateHash specifies if a unique key constraint violation exception should be
	 *            handled or not in the event we attempted to insert a duplicate hash row for the same
	 *            entity.
	 */
	public static void saveHash(BaseHashEntity object, boolean handleDuplicateHash) {
		try {
			getReceiverService().saveHash(object);
		}
		catch (Throwable e) {
			if (!handleDuplicateHash || !updateHashIfRowExists(e, object)) {
				throw e;
			}
		}
	}
	
	/**
	 * Checks if the exception is due to an attempt to insert a duplicate hash row for the same entity,
	 * and if it is the case it updates the existing hash row instead.
	 *
	 * @param throwable the thrown exception
	 * @param object the hash entity that was being inserted
	 * @return true if a duplicate hash row was found and updated otherwise false
	 */
	protected static boolean updateHashIfRowExists(Throwable throwable, BaseHashEntity object) {
		if (throwable instanceof ConstraintViolationException) {
			BaseHashEntity existing = getStoredHash(object.getIdentifier(), object.getClass());
			if (existing != null) {
				//This will typically happen if we inserted the hash but something went wrong before or during
				//insert of the entity and the event comes back as a retry item
				log.info("Found existing hash for a new entity, this could be a retry item to insert a new entity "
				        + "where the hash was created but the insert previously failed or a previously deleted entity "
				        + "by another site");
				
				existing.setHash(object.getHash());
				existing.setDateChanged(object.getDateCreated());
				
				if (log.isDebugEnabled()) {
					log.debug("Updating hash with that of the incoming entity state");
				}
				
				saveHash(existing, false);
				return true;
			}
		}
		
		return false;
	}
	
	private static ReceiverService getReceiverService() {
		if (receiverService == null) {
			receiverService = SyncContext.getBean(ReceiverService.class);
		}
		
		return receiverService;
	}
	
}
