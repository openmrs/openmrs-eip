package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class EncounterService extends AbstractEntityService<Encounter, EncounterModel> {

    public EncounterService(final SyncEntityRepository<Encounter> repository,
                            final EntityMapper<Encounter, EncounterModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.ENCOUNTER;
    }
}
