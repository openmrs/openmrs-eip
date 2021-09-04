package org.openmrs.eip.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.eip.component.service.TableToSyncEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(AppUtils.class);
	
	private final static Set<TableToSyncEnum> IGNORE_TABLES;
	
	static {
		IGNORE_TABLES = new HashSet();
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.PROVIDER_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION);
		IGNORE_TABLES.add(TableToSyncEnum.ORDER_FREQUENCY);
	}
	
	private static Map<String, String> classAndSimpleNameMap = null;
	
	/**
	 * Gets the set of names of the tables to sync
	 * 
	 * @return a set of table names
	 */
	public static Set<String> getTablesToSync() {
		Set<String> tables = new HashSet(TableToSyncEnum.values().length);
		for (TableToSyncEnum tableToSyncEnum : TableToSyncEnum.values()) {
			//TODO Remove the enum values instead including services
			if (IGNORE_TABLES.contains(tableToSyncEnum)) {
				continue;
			}
			
			tables.add(tableToSyncEnum.name());
		}
		
		return tables;
	}
	
	private static Map<String, String> getClassAndSimpleNameMap() {
		if (classAndSimpleNameMap == null) {
			synchronized (AppUtils.class) {
				if (classAndSimpleNameMap == null) {
					log.info("Initializing class to simple name mappings...");
					
					classAndSimpleNameMap = new HashMap(TableToSyncEnum.values().length);
					Arrays.stream(TableToSyncEnum.values()).forEach(e -> {
						classAndSimpleNameMap.put(e.getModelClass().getName(),
						    e.getEntityClass().getSimpleName().toLowerCase());
					});
					
					if (log.isDebugEnabled()) {
						log.debug("Class to simple name mappings: " + classAndSimpleNameMap);
					}
					
					log.info("Successfully initialized class to simple name mappings");
				}
			}
		}
		
		return classAndSimpleNameMap;
	}
	
	/**
	 * Gets the simple entity class name that matches the specified fully qualified model class name
	 *
	 * @return simple entity class name
	 */
	public static String getSimpleName(String modelClassName) {
		return getClassAndSimpleNameMap().get(modelClassName);
	}
	
}
