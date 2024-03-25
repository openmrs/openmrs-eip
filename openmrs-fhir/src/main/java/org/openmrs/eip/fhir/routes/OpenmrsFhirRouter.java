package org.openmrs.eip.fhir.routes;

import static org.apache.camel.support.builder.PredicateBuilder.and;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_SNAPSHOT;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;
import static org.openmrs.eip.fhir.Constants.PROP_FHIR_RESOURCES;
import static org.openmrs.eip.fhir.Constants.URI_FHIR_ROUTER;
import static org.openmrs.eip.fhir.utils.OpenmrsResourceTableMapper.mapResourcesToTables;
import static org.openmrs.eip.fhir.utils.OpenmrsResourceTableMapper.splitCsv;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.fhir.utils.OpenmrsResourceTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This is the base router for the OpenMRS-FHIR component. It is an end-point wired into the
 * OpenMRS-Watcher component; it filters incoming events to ensure they match one of the tables we
 * are watching and dispatches to <i>all</i> sub-routes. Sub-routes are responsible for filtering
 * the messages they care about.
 */
@Component
public class OpenmrsFhirRouter extends RouteBuilder {
	
	private final String[] monitoredTables;
	
	private final String resourceDestinations;
	
	@Autowired
	public OpenmrsFhirRouter(@Value("${" + PROP_FHIR_RESOURCES + "}") String fhirResources) {
		Set<FhirResource> enabledResources = splitCsv(fhirResources).map(OpenmrsResourceTableMapper::maybeResource)
		        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toUnmodifiableSet());
		monitoredTables = mapResourcesToTables(fhirResources).toArray(String[]::new);
		resourceDestinations = enabledResources.stream().map(FhirResource::incomingUrl).collect(Collectors.joining(","));
	}
	
	@Override
	public void configure() {
		from(URI_FHIR_ROUTER).routeId("fhir-router")
				.log(LoggingLevel.INFO, "Processing message ${body}")
		        // first filter: we do not process snapshot events, and we only process events for tables we are
		        // configured to use.
		        .filter(and(simple("${exchangeProperty." + PROP_EVENT_SNAPSHOT + "}").isEqualTo(false),
		            simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").in((Object[]) monitoredTables)))
				.log(LoggingLevel.INFO, "Dispatching to endpoints " + resourceDestinations)
		        .recipientList(constant(resourceDestinations)).parallelProcessing().end();
	}
}
