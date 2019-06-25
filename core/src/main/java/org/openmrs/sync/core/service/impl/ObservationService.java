package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ObservationService extends AbstractEntityService<Observation, ObservationModel> {

    public ObservationService(final SyncEntityRepository<Observation> repository,
                              final EntityMapper<Observation, ObservationModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.OBSERVATION;
    }
}
