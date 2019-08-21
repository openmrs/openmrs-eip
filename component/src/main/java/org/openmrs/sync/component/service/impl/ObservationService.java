package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Observation;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.ObservationModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
