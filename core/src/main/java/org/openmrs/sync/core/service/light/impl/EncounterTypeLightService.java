package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.EncounterTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class EncounterTypeLightService extends AbstractLightServiceNoContext<EncounterTypeLight> {

    public EncounterTypeLightService(final OpenMrsRepository<EncounterTypeLight> repository) {
        super(repository);
    }

    @Override
    protected EncounterTypeLight getShadowEntity(final String uuid) {
        EncounterTypeLight encounterType = new EncounterTypeLight();
        encounterType.setUuid(uuid);
        encounterType.setName(DEFAULT_STRING + " - " + uuid);
        encounterType.setCreator(DEFAULT_USER_ID);
        encounterType.setDateCreated(DEFAULT_DATE);
        return encounterType;
    }
}
