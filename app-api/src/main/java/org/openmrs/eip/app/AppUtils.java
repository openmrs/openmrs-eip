package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_BUILD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_FILE;
import static org.openmrs.eip.app.SyncConstants.DBSYNC_PROP_VERSION;
import static org.openmrs.eip.app.SyncConstants.EXECUTOR_SHUTDOWN_TIMEOUT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class AppUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(AppUtils.class);
	
	private static final int EXIT_CODE = 129;
	
	private static Properties props;
	
	private final static Set<TableToSyncEnum> IGNORE_TABLES;
	
	private static boolean shuttingDown = false;
	
	private static Pageable taskPage;
	
	static {
		IGNORE_TABLES = new HashSet();
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.PROVIDER_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION);
		IGNORE_TABLES.add(TableToSyncEnum.PROVIDER);
		IGNORE_TABLES.add(TableToSyncEnum.USERS);
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
	 * Turn on a flag which is monitored by processor threads to allow them to gracefully stop
	 * processing before the application is stopped.
	 */
	public static void handleAppContextStopping() {
		shuttingDown = true;
		log.info("Application context is stopping");
		EipFailoverTransportFactory.stopTransport();
	}
	
	/**
	 * Checks if the application is shutting down
	 */
	public static boolean isShuttingDown() {
		return shuttingDown;
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
		String version = getProperties().getProperty(DBSYNC_PROP_VERSION);
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
		String build = getProperties().getProperty(DBSYNC_PROP_BUILD_NUMBER);
		if (StringUtils.isBlank(build)) {
			log.warn("Failed to determine the application's build number");
		}
		
		return build;
	}
	
	/**
	 * Gets the {@link Pageable} object to be used by queue tasks
	 * 
	 * @return Pageable
	 */
	public static Pageable getTaskPage() {
		if (taskPage == null) {
			Environment e = SyncContext.getBean(Environment.class);
			taskPage = PageRequest.of(0, e.getProperty(PROP_SYNC_TASK_BATCH_SIZE, Integer.class, DEFAULT_TASK_BATCH_SIZE));
		}
		
		return taskPage;
	}
	
	/**
	 * Wait for all the Future instances in the specified list to terminate
	 *
	 * @param futures the list of Futures instance to wait for
	 * @param name the name of the task
	 * @throws Exception
	 */
	public static void waitForFutures(List<CompletableFuture<Void>> futures, String name) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Waiting for " + futures.size() + " " + name + " thread(s) to terminate");
		}
		
		CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		
		allFuture.get();
		
		if (log.isDebugEnabled()) {
			log.debug(futures.size() + " " + name + " thread(s) have terminated");
		}
	}
	
	/**
	 * Shuts down the specified {@link ExecutorService}
	 *
	 * @param executor the executor to shut down
	 * @param name the name of the executor
	 * @param debug specifies whether to log messages at debug level or not
	 */
	public static void shutdownExecutor(ExecutorService executor, String name, boolean debug) {
		if (debug) {
			if (log.isDebugEnabled()) {
				log.debug("Shutting down " + name + " executor");
			}
		} else {
			log.info("Shutting down " + name + " executor");
		}
		
		executor.shutdownNow();
		
		try {
			if (debug) {
				if (log.isDebugEnabled()) {
					log.debug(
					    "Waiting for " + EXECUTOR_SHUTDOWN_TIMEOUT + " seconds for " + name + " executor to terminate");
				}
			} else {
				log.info("Waiting for " + EXECUTOR_SHUTDOWN_TIMEOUT + " seconds for " + name + " executor to terminate");
			}
			
			executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
			
			if (debug) {
				if (log.isDebugEnabled()) {
					log.debug("Done shutting down " + name + " executor");
				}
			} else {
				log.info("Done shutting down " + name + " executor");
			}
		}
		catch (Exception e) {
			log.error("An error occurred while waiting for " + name + " executor to terminate");
		}
	}
	
	private static Properties getProperties() {
		if (props == null) {
			try (InputStream file = AppUtils.class.getClassLoader().getResourceAsStream(DBSYNC_PROP_FILE)) {
				Properties propsTemp = new Properties();
				propsTemp.load(file);
				props = propsTemp;
			}
			catch (IOException e) {
				throw new EIPException("Failed to load the dbsync properties file", e);
			}
		}
		
		return props;
	}
	
}
