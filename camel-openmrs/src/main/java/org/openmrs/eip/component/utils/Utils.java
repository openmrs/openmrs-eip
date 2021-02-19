package org.openmrs.eip.component.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.TestOrderModel;

public final class Utils {
	
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
	 * Gets comma-separated list of model class names surrounded with apostrophes that are subclasses or
	 * superclasses of the specified class name.
	 *
	 * @param modelClass the model class to inspect
	 * @return a list of model class names
	 */
	public static List<String> getListOfModelClassHierarchy(String modelClass) {
		//TODO This logic should be extensible
		List<String> tables = new ArrayList();
		tables.add(modelClass);
		if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			tables.add(
			    PersonModel.class.getName().equals(modelClass) ? PatientModel.class.getName() : PersonModel.class.getName());
		} else if (OrderModel.class.getName().equals(modelClass)) {
			tables.add(TestOrderModel.class.getName());
			tables.add(DrugOrderModel.class.getName());
		} else if (TestOrderModel.class.getName().equals(modelClass) || DrugOrderModel.class.getName().equals(modelClass)) {
			tables.add(OrderModel.class.getName());
		}
		
		return tables;
	}
	
	/**
	 * Gets all the model classes that are subclasses or superclass of the specified class name.
	 *
	 * @param modelClass the model class to inspect
	 * @return a comma-separated list of model class names
	 */
	public static String getModelClassesInHierarchy(String modelClass) {
		List<String> classes = getListOfModelClassHierarchy(modelClass);
		return String.join(",", classes.stream().map(clazz -> "'" + clazz + "'").collect(Collectors.toList()));
	}
	
}
