package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class PatientLightService extends AbstractLightServiceNoContext<PatientLight> {

    public PatientLightService(final OpenMrsRepository<PatientLight> repository) {
        super(repository);
    }

    @Override
    protected PatientLight getShadowEntity(final String uuid) {
        PatientLight patient = new PatientLight();
        patient.setUuid(uuid);
        patient.setAllergyStatus(DEFAULT_STRING);
        patient.setCreator(DEFAULT_USER_ID);
        patient.setPatientCreator(DEFAULT_USER_ID);
        patient.setDateCreated(DEFAULT_DATE);
        patient.setPatientDateCreated(DEFAULT_DATE);
        return patient;
    }
}
