package org.openmrs.eip.web.contoller;

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
	
}
