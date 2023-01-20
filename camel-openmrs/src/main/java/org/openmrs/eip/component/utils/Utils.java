package org.openmrs.eip.component.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.TestOrderModel;

public final class Utils {
	
	private static Map<String, List<String>> TABLE_HIERARCHY_MAP = new ConcurrentHashMap();
	
	private static Map<String, List<String>> CLASS_HIERARCHY_MAP = new ConcurrentHashMap();
	
	/**
	 * Gets comma-separated list of table names surrounded with apostrophes associated to the specified
	 * table as tables of subclass or superclass entities.
	 *
	 * @param tableName the table name
	 * @return a list of table names
	 */
	public static List<String> getListOfTablesInHierarchy(String tableName) {
		List<String> tables = TABLE_HIERARCHY_MAP.get(tableName);
		if (tables != null) {
			return tables;
		}
		
		//TODO This logic should be extensible
		tables = new ArrayList();
		tables.add(tableName);
		if ("person".equalsIgnoreCase(tableName) || "patient".equalsIgnoreCase(tableName)) {
			tables.add("person".equalsIgnoreCase(tableName) ? "patient" : "person");
		} else if ("orders".equalsIgnoreCase(tableName)) {
			tables.add("test_order");
			tables.add("drug_order");
		} else if ("test_order".equalsIgnoreCase(tableName) || "drug_order".equalsIgnoreCase(tableName)) {
			tables.add("orders");
		}
		
		TABLE_HIERARCHY_MAP.put(tableName, tables);
		
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
	 * Gets comma-separated list of model class names surrounded with apostrophes that are subclasses or
	 * superclasses of the specified class name.
	 *
	 * @param modelClass the model class name
	 * @return a list of model class names
	 */
	public static List<String> getListOfModelClassHierarchy(String modelClass) {
		List<String> classes = CLASS_HIERARCHY_MAP.get(modelClass);
		if (classes != null) {
			return classes;
		}
		
		//TODO This logic should be extensible
		classes = new ArrayList();
		classes.add(modelClass);
		if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			classes.add(
			    PersonModel.class.getName().equals(modelClass) ? PatientModel.class.getName() : PersonModel.class.getName());
		} else if (OrderModel.class.getName().equals(modelClass)) {
			classes.add(TestOrderModel.class.getName());
			classes.add(DrugOrderModel.class.getName());
		} else if (TestOrderModel.class.getName().equals(modelClass) || DrugOrderModel.class.getName().equals(modelClass)) {
			classes.add(OrderModel.class.getName());
		}
		
		CLASS_HIERARCHY_MAP.put(modelClass, classes);
		
		return classes;
	}
	
	/**
	 * Gets all the model classes that are subclasses or superclass of the specified class name.
	 *
	 * @param modelClass the model class to inspect
	 * @return a comma-separated list of model class names
	 */
	public static String getModelClassesInHierarchy(String modelClass) {
		//TODO cache the return value
		List<String> classes = getListOfModelClassHierarchy(modelClass);
		return String.join(",", classes.stream().map(clazz -> "'" + clazz + "'").collect(Collectors.toList()));
	}
	
	/**
	 * Checks if the specified table is for a subclass entity
	 * 
	 * @param tableName the table name to check
	 * @return true for a subclass table otherwise false
	 */
	public static boolean isSubclassTable(String tableName) {
		return Constants.SUBCLASS_TABLES.contains(tableName.toLowerCase());
	}
	
}
