package org.openmrs.eip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class Utils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	
	private static boolean shuttingDown = false;
	
	/**
	 * Gets a list of all watched table names
	 * 
	 * @return
	 */
	public static List<String> getWatchedTables() {
		String watchedTables = AppContext.getBean(Environment.class).getProperty(Constants.PROP_WATCHED_TABLES);
		return Arrays.asList(watchedTables.split(","));
	}
	
	/**
	 * Gets comma-separated list of table names surrounded with apostrophes associated to the specified
	 * table as tables of subclass or superclass entities.
	 *
	 * @param tableName the tables to inspect
	 * @return a list of table names
	 */
	public static List<String> getListOfTablesInHierarchy(String tableName) {
		//TODO This logic should be extensible
		List<String> tables = new ArrayList();
		tables.add(tableName);
		if ("person".equalsIgnoreCase(tableName) || "patient".equalsIgnoreCase(tableName)) {
			tables.add("person".equalsIgnoreCase(tableName) ? "patient" : "person");
		} else if ("orders".equalsIgnoreCase(tableName)) {
			tables.addAll(Constants.ORDER_SUBCLASS_TABLES);
		} else if (Constants.ORDER_SUBCLASS_TABLES.contains(tableName.toLowerCase())) {
			tables.add("orders");
		}
		
		return tables;
	}
	
	/**
	 * Gets the tables associated to the specified table as tables of subclass or superclass entities.
	 *
	 * @param tableName the tables to inspect
	 * @return a comma-separated list of table names
	 */
	public static String getTablesInHierarchy(String tableName) {
		List<String> tables = getListOfTablesInHierarchy(tableName);
		return String.join(",", tables.stream().map(tName -> "'" + tName + "'").collect(Collectors.toList()));
	}
	
	/**
	 * Gets the current seconds since the epoch
	 * 
	 * @return the difference in seconds between the current time and the epoch
	 */
	public static long getCurrentSeconds() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * Sets the shutdown flag
	 */
	public static void setShuttingDown() {
		shuttingDown = true;
		LOGGER.info("Received application shutting down event");
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
		if (isShuttingDown()) {
			LOGGER.info("Application is already shutting down");
			return;
		}
		
		setShuttingDown();
		
		LOGGER.info("Shutting down the application...");
		
		//Shutdown in a new thread to ensure other background shutdown threads complete too
		new Thread(() -> System.exit(129)).start();
	}
	
	/**
	 * Checks if the specified table name is orders or an order subclass tables
	 *
	 * @param tableName the table name to check
	 * @return true if the specified table name is orders or an order subclass tables otherwise false
	 */
	public static boolean isOrderTable(String tableName) {
		return Constants.ORDER_TABLES.contains(tableName.toLowerCase());
	}
	
	/**
	 * Shuts down the specified executor
	 * 
	 * @param executor the {@link ExecutorService} instance
	 * @param name executor name
	 * @param timeout the timeout to apply when shutting down the executor
	 */
	public static void shutdownExecutor(ExecutorService executor, String name, int timeout) {
		LOGGER.info("Shutting down " + name + " executor");
		
		executor.shutdownNow();
		
		try {
			LOGGER.info("Waiting for " + timeout + " seconds for " + name + " executor to terminate");
			
			executor.awaitTermination(timeout, TimeUnit.SECONDS);
			
			LOGGER.info("Done shutting down " + name + " executor");
		}
		catch (Exception e) {
			LOGGER.error("An error occurred while waiting for " + name + " executor to terminate");
		}
	}
	
}
