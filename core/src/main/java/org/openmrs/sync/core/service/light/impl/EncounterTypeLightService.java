package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.EncounterTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class EncounterTypeLightService extends AbstractLightService<EncounterTypeLight> {

    public EncounterTypeLightService(final OpenMrsRepository<EncounterTypeLight> repository) {
        super(repository);
    }

    @Override
    protected EncounterTypeLight createPlaceholderEntity(final String uuid) {
        EncounterTypeLight encounterType = new EncounterTypeLight();
        encounterType.setName(DEFAULT_STRING + " - " + uuid);
        encounterType.setCreator(DEFAULT_USER_ID);
        encounterType.setDateCreated(DEFAULT_DATE);
        return encounterType;
    }
}
