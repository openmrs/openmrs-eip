package org.openmrs.eip.fhir;

import java.util.regex.Pattern;

public final class Constants {
	
	public static final Pattern CSV_PATTERN = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	
	public static final String HEADER_FHIR_EVENT_TYPE = "openmrs.fhir.event";
	
	public static final String PROP_EVENT_SNAPSHOT = "event.snapshot";
	
	public static final String PROP_EVENT_IDENTIFIER = "event.identifier";
	
	public static final String PROP_EVENT_OPERATION = "event.operation";
	
	public static final String PROP_EVENT_TABLE_NAME = "event.tableName";
	
	public static final String PROP_FHIR_RESOURCES = "eip.fhir.resources";
	
	public static final String URI_FHIR_ROUTER = "direct:openmrs-fhir-router";
	
	public static final String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
	
	public static final String IMAGING_ORDER_TYPE_UUID = "8d2aff07-55e6-4a4a-8878-72b9eb36a3b8";
	
	public static final String PROCEDURE_ORDER_TYPE_UUID = "67a92e56-0f88-11ea-8d71-362b9e155667";
	
	public static final String MEDICAL_SUPPLY_ORDER_TYPE_UUID = "67a92bd6-0f88-11ea-8d71-362b9e155667";
	
	public static final String DRUG_ORDER_TYPE_UUID = "131168f4-15f5-102d-96e4-000c29c2a5d7";
	
	private Constants() {
	};
}
