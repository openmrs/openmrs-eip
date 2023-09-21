package org.openmrs.eip.web.contoller;

import java.util.List;

import org.openmrs.eip.web.Dashboard;

/**
 * Marker interface for classes that can generate a {@link Dashboard}
 */
public interface DashboardGenerator {
	
	/**
	 * Generates a dashboard
	 *
	 * @return Dashboard the generated dashboard
	 */
	Dashboard generate();
	
	/**
	 * Gets the grouping names for the dashboard entries
	 *
	 * @return the list of groups
	 */
	List<String> getGroups();
	
}
