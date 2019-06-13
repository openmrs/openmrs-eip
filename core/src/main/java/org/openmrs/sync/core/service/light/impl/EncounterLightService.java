package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.EncounterLight;
import org.openmrs.sync.core.entity.light.EncounterTypeLight;
import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.EncounterContext;
import org.springframework.stereotype.Service;

@Service
public class EncounterLightService extends AbstractLightService<EncounterLight, EncounterContext> {

    private LightServiceNoContext<PatientLight> patientService;

    private LightServiceNoContext<EncounterTypeLight> encounterTypeService;

    public EncounterLightService(final OpenMrsRepository<EncounterLight> repository,
                                 final LightServiceNoContext<PatientLight> patientService,
                                 final LightServiceNoContext<EncounterTypeLight> encounterTypeService) {
        super(repository);
        this.patientService = patientService;
        this.encounterTypeService = encounterTypeService;
    }

    @Override
    protected EncounterLight getShadowEntity(final String uuid, final EncounterContext context) {
        EncounterLight encounter = new EncounterLight();
        encounter.setUuid(uuid);
        encounter.setDateCreated(DEFAULT_DATE);
        encounter.setCreator(DEFAULT_USER_ID);
        encounter.setEncounterType(encounterTypeService.getOrInit(context.getEncounterTypeUuid()));
        encounter.setEncounterDatetime(DEFAULT_DATE);
        encounter.setPatient(patientService.getOrInit(context.getPatientUuid()));
        return encounter;
    }
}
