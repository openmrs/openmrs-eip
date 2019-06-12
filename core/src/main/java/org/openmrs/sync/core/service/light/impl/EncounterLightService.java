package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.EncounterLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncounterLightService extends AbstractLightService<EncounterLight> {

    private PatientLightService patientService;

    private EncounterTypeLightService encounterTypeService;

    public EncounterLightService(final OpenMrsRepository<EncounterLight> repository,
                                 final PatientLightService patientService,
                                 final EncounterTypeLightService encounterTypeService) {
        super(repository);
        this.patientService = patientService;
        this.encounterTypeService = encounterTypeService;
    }

    @Override
    protected EncounterLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        EncounterLight encounter = new EncounterLight();
        encounter.setUuid(uuid);
        encounter.setDateCreated(DEFAULT_DATE);
        encounter.setCreator(DEFAULT_USER_ID);
        encounter.setEncounterType(encounterTypeService.getOrInit(AttributeHelper.getEncounterTypeUuid(uuids)));
        encounter.setEncounterDatetime(DEFAULT_DATE);
        encounter.setPatient(patientService.getOrInit(AttributeHelper.getPatientUuid(uuids)));
        return encounter;
    }
}
