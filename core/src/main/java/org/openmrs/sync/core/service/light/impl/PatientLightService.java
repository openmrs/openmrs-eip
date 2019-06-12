package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientLightService extends AbstractLightService<PatientLight> {

    public PatientLightService(final OpenMrsRepository<PatientLight> repository) {
        super(repository);
    }

    @Override
    protected PatientLight getFakeEntity(final String uuid,
                                         final List<AttributeUuid> attributeUuids) {
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
