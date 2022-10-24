package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PatientIdentifierTypeLightService extends AbstractLightService<PatientIdentifierTypeLight> {
	
	public PatientIdentifierTypeLightService(final OpenmrsRepository<PatientIdentifierTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected PatientIdentifierTypeLight createPlaceholderEntity(final String uuid) {
		PatientIdentifierTypeLight patientIdentifierType = new PatientIdentifierTypeLight();
		patientIdentifierType.setDateCreated(DEFAULT_DATE);
		patientIdentifierType.setCreator(SyncContext.getAppUser().getId());
		patientIdentifierType.setName(DEFAULT_STRING);
		return patientIdentifierType;
	}
}
