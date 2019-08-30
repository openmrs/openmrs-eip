package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.model.EncounterModel;
import org.openmrs.sync.component.entity.Encounter;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
