package org.openmrs.eip.fhir.spring;

import static org.openmrs.eip.Constants.PROP_WATCHED_TABLES;
import static org.openmrs.eip.fhir.Constants.URI_FHIR_ROUTER;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT_DESTINATIONS;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openmrs.eip.fhir.utils.OpenmrsResourceTableMapper;
import org.springframework.core.env.PropertySource;

/**
 * This is a Spring {@link PropertySource}, used to automatically wire the openmrs-watcher, so that
 * it feeds events into this module's {@link org.openmrs.eip.fhir.routes.OpenmrsFhirRouter}, which
 * contains the main handling logic for Openmrs-based FHIR events.
 */
public class OpenmrsFhirPropertySource extends PropertySource<Properties> {
	
	public OpenmrsFhirPropertySource(String resources, String baseWatches, String baseDbEventDestinations) {
		super("Openmrs FHIR Property Source", new Properties());
		
		getSource()
		        .setProperty(
		            PROP_WATCHED_TABLES, Stream
		                    .concat(OpenmrsResourceTableMapper.mapResourcesToTables(resources),
		                        OpenmrsResourceTableMapper.splitCsv(baseWatches))
		                    .distinct().collect(Collectors.joining(",")));
		
		getSource().setProperty(PROP_EVENT_DESTINATIONS,
		    baseDbEventDestinations != null && !baseDbEventDestinations.isBlank()
		            ? baseDbEventDestinations + "," + URI_FHIR_ROUTER
		            : URI_FHIR_ROUTER);
	}
	
	@Override
	public Object getProperty(String name) {
		return getSource().getProperty(name);
	}
}
