package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.PatientLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PatientLightService extends AbstractLightService<PatientLight> {

    public PatientLightService(final OpenMrsRepository<PatientLight> repository) {
        super(repository);
    }

    @Override
    protected PatientLight createPlaceholderEntity(final String uuid) {
        PatientLight patient = new PatientLight();
        patient.setAllergyStatus(DEFAULT_STRING);
        patient.setCreator(DEFAULT_USER_ID);
        patient.setPatientCreator(DEFAULT_USER_ID);
        patient.setDateCreated(DEFAULT_DATE);
        patient.setPatientDateCreated(DEFAULT_DATE);
        return patient;
    }
}
