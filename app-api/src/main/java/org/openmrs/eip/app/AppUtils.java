package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_BUILD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_FILE;
import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_VERSION;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(AppUtils.class);
	
	private static final int EXIT_CODE = 129;
	
	private static final Properties PROPERTIES;
	
	private static boolean appContextStopping = false;
	
	private static boolean shuttingDown = false;
	
	private final static Set<TableToSyncEnum> IGNORE_TABLES;
	
	private final static List<String> SUBCLASS_TABLES = Collections.unmodifiableList(
	    Arrays.asList(TableToSyncEnum.PATIENT.name(), TableToSyncEnum.DRUG_ORDER.name(), TableToSyncEnum.TEST_ORDER.name()));
	
	static {
		IGNORE_TABLES = new HashSet();
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.PROVIDER_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION);
		
		try (InputStream file = SyncConstants.class.getClassLoader().getResourceAsStream(DBSYNC_PROP_FILE)) {
			PROPERTIES = new Properties();
			PROPERTIES.load(file);
		}
		catch (IOException e) {
			throw new EIPException("Failed to load the dbsync properties file: ", e);
		}
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
	
	public static Map<String, String> getClassAndSimpleNameMap() {
		synchronized (AppUtils.class) {
			if (classAndSimpleNameMap == null) {
				log.info("Initializing class to simple name mappings...");
				
				classAndSimpleNameMap = new HashMap(TableToSyncEnum.values().length);
				Arrays.stream(TableToSyncEnum.values()).forEach(e -> {
					classAndSimpleNameMap.put(e.getModelClass().getName(), e.getEntityClass().getSimpleName().toLowerCase());
				});
				
				if (log.isDebugEnabled()) {
					log.debug("Class to simple name mappings: " + classAndSimpleNameMap);
				}
				
				log.info("Successfully initialized class to simple name mappings");
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
	
	/**
	 * Checks if the specified table is for a subclass or not
	 * 
	 * @param tableName the name of the table to check
	 * @return true for a subclass table otherwise false
	 */
	public static boolean isSubclassTable(String tableName) {
		return SUBCLASS_TABLES.contains(tableName.toUpperCase());
	}
	
	/**
	 * Turn on a flag which is monitored by processor threads to allow them to gracefully stop
	 * processing before the application is stopped.
	 */
	public static void setAppContextStopping() {
		appContextStopping = true;
		EipFailoverTransportFactory.stopTransport();
		log.info("Application context is stopping");
	}
	
	/**
	 * Checks if the application context is stopping
	 *
	 * @return true if the application context is stopping
	 */
	public static boolean isAppContextStopping() {
		return appContextStopping;
	}
	
	/**
	 * Checks if the application is shutting down
	 */
	public static boolean isShuttingDown() {
		return shuttingDown;
	}
	
	/**
	 * Checks if the application is stopping
	 */
	public static boolean isStopping() {
		return isAppContextStopping() || isShuttingDown();
	}
	
	/**
	 * Shuts down the application
	 */
	public synchronized static void shutdown() {
		shutdown(true);
	}
	
	/**
	 * Shuts down the application
	 * 
	 * @param async specifies if the shutdown should happen asynchronously or not
	 */
	public synchronized static void shutdown(boolean async) {
		if (isShuttingDown()) {
			return;
		}
		
		shuttingDown = true;
		
		log.info("Shutting down the application...");
		
		if (async) {
			//Shutdown in a new thread to ensure other background framework shutdown threads complete too
			new Thread(() -> System.exit(EXIT_CODE)).start();
		} else {
			System.exit(EXIT_CODE);
		}
	}
	
	/**
	 * Gets the application version
	 *
	 * @return application version
	 */
	public static String getVersion() {
		String version = PROPERTIES.getProperty(DBSYNC_PROP_VERSION);
		if (StringUtils.isBlank(version)) {
			log.warn("Failed to determine the application version");
		}
		
		return version;
	}
	
	/**
	 * Gets the buildnumber
	 *
	 * @return application version
	 */
	public static String getBuildNumber() {
		String build = PROPERTIES.getProperty(DBSYNC_PROP_BUILD_NUMBER);
		if (StringUtils.isBlank(build)) {
			log.warn("Failed to determine the application's build number");
		}
		
		return build;
	}
	
}
