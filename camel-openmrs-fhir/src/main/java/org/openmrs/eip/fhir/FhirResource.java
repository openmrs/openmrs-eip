package org.openmrs.eip.fhir;

@SuppressWarnings("unused")
// TODO There are more resources supported by the FHIR2 module and possibly more sub-tables
// TODO Should this be configurable rather than hard-coded?
public enum FhirResource {
    
    // TODO Should we separate Visit from Encounter?
	ENCOUNTER("direct:fhir-handler-encounter", "direct:fhir-encounter", "encounter", "visit"),
	
	MEDICATIONREQUEST("direct:fhir-handler-medicationrequest", "direct:fhir-medicationrequest", "orders", "drug_order"),
	
	OBSERVATION("direct:fhir-handler-obs", "direct:fhir-obs", "obs"),
	
	PATIENT(
	        "direct:fhir-handler-patient",
	        "direct:fhir-patient",
	        "patient",
	        "patient_identifier",
	        "person",
	        "person_address",
	        "person_name"),
	
	PERSON("direct:fhir-handler-person", "direct:fhir-person", "person", "person_address", "person_name"),
	
	PRACTITIONER(
	        "direct:fhir-handler-practitioner",
	        "direct:fhir-practitioner",
	        "users",
	        "provider",
	        "person",
	        "person_name"),
	
	SERVICEREQUEST("direct:fhir-handler-servicerequest", "direct:fhir-servicerequest", "orders", "test_order"),
	
	PROCEDURE("direct:fhir-handler-procedure", "direct:fhir-procedure", "orders", "procedure_order"),
	
	SUPPLYREQUEST("direct:fhir-handler-supplyrequest", "direct:fhir-supplyrequest", "orders"),
	
	TASK("direct:fhir-handler-task", "direct:fhir-task", "fhir_task");
	
	private final String[] tables;
	
	private final String incomingUrl;
	
	private final String outgoingUrl;
	
	FhirResource(String incomingUrl, String outgoingUrl, String... tables) {
		this.incomingUrl = incomingUrl;
		this.outgoingUrl = outgoingUrl;
		this.tables = tables;
	}
	
	public String incomingUrl() {
		return incomingUrl;
	}
	
	public String outgoingUrl() {
		return outgoingUrl;
	}
	
	public String[] tables() {
		return tables;
	};
}
