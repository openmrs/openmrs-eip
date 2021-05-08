package org.openmrs.eip;

import java.util.Arrays;
import java.util.List;

public class Utils {
	
	private static final String[] WATCHED_TABLES = new String[] { "PERSON", "PATIENT", "VISIT", "ENCOUNTER", "OBS",
	        "PERSON_ATTRIBUTE", "PATIENT_PROGRAM", "PATIENT_STATE", "VISIT_ATTRIBUTE", "ENCOUNTER_DIAGNOSIS", "CONDITION",
	        "PERSON_NAME", "ALLERGY", "PERSON_ADDRESS", "PATIENT_IDENTIFIER", "ORDERS", "DRUG_ORDER", "TEST_ORDER" };
	
	/**
	 * Gets a list of all watched table names
	 * 
	 * @return
	 */
	public static List<String> getWatchedTables() {
		return Arrays.asList(WATCHED_TABLES);
	}
	
}
