package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.EncounterProvider;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.EncounterProviderModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class EncounterProviderService extends AbstractEntityService<EncounterProvider, EncounterProviderModel> {

    public EncounterProviderService(final SyncEntityRepository<EncounterProvider> repository,
                                     final EntityToModelMapper<EncounterProvider, EncounterProviderModel> entityToModelMapper,
                                     final ModelToEntityMapper<EncounterProviderModel, EncounterProvider> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ENCOUNTER_PROVIDER;
    }
}
