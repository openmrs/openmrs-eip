package org.openmrs.eip.app;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.eip.component.service.TableToSyncEnum;

public class AppUtils {
	
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
	
}
