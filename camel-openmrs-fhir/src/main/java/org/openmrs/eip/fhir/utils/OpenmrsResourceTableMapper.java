package org.openmrs.eip.fhir.utils;

import static org.openmrs.eip.fhir.Constants.CSV_PATTERN;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.openmrs.eip.fhir.FhirResource;

/**
 * Utility class to assist with mapping between FHIR resources and OpenMRS tables.
 */
public class OpenmrsResourceTableMapper {
	
	private OpenmrsResourceTableMapper() {
	}
	
	public static Stream<String> mapResourcesToTables(String resources) {
		return splitCsv(resources).map(OpenmrsResourceTableMapper::resourceToWatches).filter(Optional::isPresent)
		        .flatMap(Optional::get);
	}
	
	public static Stream<String> splitCsv(String resources) {
		if (resources == null || resources.isBlank()) {
			return Stream.empty();
		}
		
		return CSV_PATTERN.splitAsStream(resources).map(String::toUpperCase);
	}
	
	public static Optional<FhirResource> maybeResource(String resource) {
		if (resource == null || resource.isBlank()) {
			return Optional.empty();
		}
		
		try {
			return Optional.of(FhirResource.valueOf(resource.toUpperCase(Locale.ROOT)));
		}
		catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	protected static Optional<Stream<String>> resourceToWatches(String resource) {
		return maybeResource(resource).map(r -> Stream.of(r.tables()));
	}
}
