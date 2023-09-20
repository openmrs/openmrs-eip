package org.openmrs.eip.web.contoller;

import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.DashboardMetadata;

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
	 * Creates the {@link org.openmrs.eip.web.DashboardMetadata}
	 *
	 * @return the generated DashboardMetadata
	 */
	DashboardMetadata createMetadata();
	
}
