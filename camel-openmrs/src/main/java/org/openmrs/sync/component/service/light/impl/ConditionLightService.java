package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.ConditionLight;
import org.openmrs.sync.component.entity.light.PatientLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.openmrs.sync.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class ConditionLightService extends AbstractLightService<ConditionLight> {

    private LightService<PatientLight> patientService;

    public ConditionLightService(final OpenmrsRepository<ConditionLight> repository,
                                 final LightService<PatientLight> patientService) {
        super(repository);
        this.patientService = patientService;
    }

    @Override
    protected ConditionLight createPlaceholderEntity(final String uuid) {
        ConditionLight condition = new ConditionLight();
        condition.setDateCreated(DEFAULT_DATE);
        condition.setCreator(DEFAULT_USER_ID);
        condition.setClinicalStatus(DEFAULT_STRING);
        condition.setPatient(patientService.getOrInitPlaceholderEntity());
        return condition;
    }
}
