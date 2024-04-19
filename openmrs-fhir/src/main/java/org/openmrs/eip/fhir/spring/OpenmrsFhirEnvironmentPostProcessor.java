package org.openmrs.eip.fhir.spring;

import static org.openmrs.eip.Constants.PROP_WATCHED_TABLES;
import static org.openmrs.eip.fhir.Constants.PROP_FHIR_RESOURCES;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT_DESTINATIONS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

/**
 * An {@link EnvironmentPostProcessor} to set up the openmrs-watcher in line with the openmrs-fhir
 * configuration. Specifically, this {@code PostProcessor} is responsible for setting
 * `eip.watchedTables` based on the FHIR resources selected in `eip.fhirResources` and that
 * `db-event.destinations` routes to the main FHIR router.
 */
// lowest precedence so this is always overridable
@Order
public class OpenmrsFhirEnvironmentPostProcessor implements EnvironmentPostProcessor {
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String resources = environment.getProperty(PROP_FHIR_RESOURCES);
		if (resources == null || resources.isEmpty()) {
			return;
		}
		
		String baseWatches = environment.getProperty(PROP_WATCHED_TABLES);
		String dbEventSinks = environment.getProperty(PROP_EVENT_DESTINATIONS);
		
		MutablePropertySources propertySources = environment.getPropertySources();
		// addFirst because we're trying to overwrite existing properties
		propertySources.addFirst(new OpenmrsFhirPropertySource(resources, baseWatches, dbEventSinks));
	}
}
