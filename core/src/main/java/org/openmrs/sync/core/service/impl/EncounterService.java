package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class EncounterService extends AbstractEntityService<Encounter, EncounterModel> {

    public EncounterService(final SyncEntityRepository<Encounter> repository,
                            final EntityToModelMapper<Encounter, EncounterModel> entityToModelMapper,
                            final ModelToEntityMapper<EncounterModel, Encounter> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ENCOUNTER;
    }
}
