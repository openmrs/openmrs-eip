package org.openmrs.eip.component.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepositoryImpl {
	
	private PatientRepository patientRepository;
	
	public PatientRepositoryImpl(@Lazy final PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}
	
	private List<String> parseMappings(final String workflowStateConceptMappingsString) {
		return Arrays.asList(workflowStateConceptMappingsString.split(";"));
	}
}
