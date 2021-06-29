package org.openmrs.eip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	
	private static final String[] WATCHED_TABLES = new String[] { "PERSON", "PATIENT", "VISIT", "ENCOUNTER", "OBS",
	        "PERSON_ATTRIBUTE", "PATIENT_PROGRAM", "PATIENT_STATE", "VISIT_ATTRIBUTE", "ENCOUNTER_DIAGNOSIS", "CONDITION",
	        "PERSON_NAME", "ALLERGY", "PERSON_ADDRESS", "PATIENT_IDENTIFIER", "ORDERS", "DRUG_ORDER", "TEST_ORDER",
	        "RELATIONSHIP", "ENCOUNTER_PROVIDER", "ORDER_GROUP", "PATIENT_PROGRAM_ATTRIBUTE" };
	
	/**
	 * Gets a list of all watched table names
	 * 
	 * @return
	 */
	public static List<String> getWatchedTables() {
		return Arrays.asList(WATCHED_TABLES);
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
	
}
