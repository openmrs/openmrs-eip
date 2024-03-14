package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.fhir.FhirJsonDataFormat;
import org.openmrs.eip.fhir.FhirResource;

public abstract class BaseFhirResourceRouter extends RouteBuilder {
	
	protected final FhirJsonDataFormat DEFAULT_FORMAT = new FhirJsonDataFormat();
	{
		DEFAULT_FORMAT.setFhirVersion("R4");
	}
	
	protected String[] supportedTables;
	
	BaseFhirResourceRouter(FhirResource resource) {
		supportedTables = resource.tables();
	}
	
	protected Predicate isSupportedTable() {
		return exchangeProperty(PROP_EVENT_TABLE_NAME).in((Object[]) supportedTables);
	}
}
