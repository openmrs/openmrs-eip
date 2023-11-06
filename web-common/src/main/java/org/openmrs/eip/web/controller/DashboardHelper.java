package org.openmrs.eip.web.controller;

import java.util.List;

import org.openmrs.eip.component.SyncOperation;

/**
 * Marker interface for helper classes used by the {@link DashboardController}
 */
public interface DashboardHelper {
	
	/**
	 * Gets the categorization property name for the entities handled by this generated
	 * 
	 * @param entityType the entity type
	 * @return the categorization property name
	 */
	String getCategorizationProperty(String entityType);
	
	/**
	 * Gets the unique category names for the entities handled by this generated
	 * 
	 * @param entityType entity type name
	 * @return the list of categories
	 */
	List<String> getCategories(String entityType);
	
	/**
	 * Gets the count of items in the queue for the specified entity type matching the specified
	 * category and operation
	 * 
	 * @param entityType entity type name
	 * @param category category name
	 * @param op operation
	 * @return count of items
	 */
	Integer getCount(String entityType, String category, SyncOperation op);
	
}
