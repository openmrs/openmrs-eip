package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PatientLightService extends AbstractLightService<PatientLight> {
	
	public PatientLightService(final OpenmrsRepository<PatientLight> repository) {
		super(repository);
	}
	
	@Override
	protected PatientLight createPlaceholderEntity(final String uuid) {
		PatientLight patient = new PatientLight();
		patient.setAllergyStatus(DEFAULT_STRING);
		patient.setCreator(SyncContext.getAppUser().getId());
		patient.setPatientCreator(SyncContext.getAppUser().getId());
		patient.setDateCreated(DEFAULT_DATE);
		patient.setPatientDateCreated(DEFAULT_DATE);
		return patient;
	}
}
