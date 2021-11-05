package org.openmrs.eip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;

public class Utils {
	
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
			tables.add("test_order");
			tables.add("drug_order");
		} else if ("test_order".equalsIgnoreCase(tableName) || "drug_order".equalsIgnoreCase(tableName)) {
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
	 * Shuts down the application
	 */
	public static void shutdown() {
		//Shutdown in a new thread to ensure other background shutdown threads complete too
		new Thread(() -> System.exit(129)).start();
	}
	
}
