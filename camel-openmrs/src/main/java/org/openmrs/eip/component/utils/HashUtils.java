package org.openmrs.eip.component.utils;

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

import org.apache.camel.ProducerTemplate;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(HashUtils.class);
	
	private static Map<Class<? extends BaseModel>, Set<String>> modelClassDatetimePropsMap = null;
	
	/**
	 * Computes the hash of the specified model, the logic is such that it removes null values, extracts
	 * uuids for all light entity fields, converts datetime fields to milliseconds since the epoch,
	 * sorts the values by field names, Stringifies the values, trims the values and concatenates all
	 * values into a single string which is then hashed, this implementation has implications below,
	 *
	 * <pre>
	 * 	  - It is case sensitive
	 * 	  - Field value changes from null to an empty string or what space characters and vice versa are ignored
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
	 * @param producerTemplate ProducerTemplate object
	 * @return the saved hash entity object otherwise null
	 */
	public static BaseHashEntity getStoredHash(String identifier, Class<? extends BaseHashEntity> hashClass,
	                                           ProducerTemplate producerTemplate) {
		
		final String query = Constants.QUERY_GET_HASH.replace(Constants.PLACEHOLDER_CLASS, hashClass.getSimpleName())
		        .replace(Constants.PLACEHOLDER_UUID, identifier);
		List<? extends BaseHashEntity> hashes = producerTemplate.requestBody(query, null, List.class);
		
		BaseHashEntity hash = null;
		if (hashes != null && hashes.size() == 1) {
			hash = hashes.get(0);
		}
		
		return hash;
	}
	
}
