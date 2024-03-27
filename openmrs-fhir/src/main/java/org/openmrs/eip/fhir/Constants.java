package org.openmrs.eip.fhir;

public final class Constants {
	
	public static final String HEADER_FHIR_EVENT_TYPE = "openmrs.fhir.event";
	
	public static final String PROP_EVENT_SNAPSHOT = "event.snapshot";
	
	public static final String PROP_EVENT_IDENTIFIER = "event.identifier";
	
	public static final String PROP_EVENT_OPERATION = "event.operation";
	
	public static final String PROP_EVENT_TABLE_NAME = "event.tableName";
	
	public static final String PROP_FHIR_RESOURCES = "eip.fhir.resources";
	
	public static final String URI_FHIR_ROUTER = "direct:openmrs-fhir-router";
	
	private Constants() {
	};
}
