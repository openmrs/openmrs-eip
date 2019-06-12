package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.EncounterTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncounterTypeLightService extends AbstractLightService<EncounterTypeLight> {

    public EncounterTypeLightService(final OpenMrsRepository<EncounterTypeLight> repository) {
        super(repository);
    }

    @Override
    protected EncounterTypeLight getFakeEntity(final String uuid,
                                               final List<AttributeUuid> attributeUuids) {
        EncounterTypeLight encounterType = new EncounterTypeLight();
        encounterType.setUuid(uuid);
        encounterType.setName(DEFAULT_STRING + " - " + uuid);
        encounterType.setCreator(DEFAULT_USER_ID);
        encounterType.setDateCreated(DEFAULT_DATE);
        return encounterType;
    }
}
