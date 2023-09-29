package org.openmrs.eip.web.controller;

import java.util.List;

import org.openmrs.eip.web.Dashboard;

/**
 * Marker interface for classes that can generate a {@link Dashboard} data.
 */
public interface DashboardGenerator {
	
	/**
	 * Generates a dashboard
	 *
	 * @return Dashboard the generated dashboard
	 */
	Dashboard generate();
	
	/**
	 * Gets the categorization property name for the entities handled by this generated
	 * 
	 * @return the categorization property name
	 */
	String getCategorizationProperty();
	
	/**
	 * Gets the unique category names for the entities handled by this generated
	 *
	 * @return the list of categories
	 */
	List<String> getCategories(String entityName);
	
}
