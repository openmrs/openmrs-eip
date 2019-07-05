package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ObservationService extends AbstractEntityService<Observation, ObservationModel> {

    public ObservationService(final SyncEntityRepository<Observation> repository,
                              final EntityToModelMapper<Observation, ObservationModel> entityToModelMapper,
                              final ModelToEntityMapper<ObservationModel, Observation> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.OBSERVATION;
    }
}
