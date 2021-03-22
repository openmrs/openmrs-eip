package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Gaac;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.GaacModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class GaacService extends AbstractEntityService<Gaac, GaacModel> {

    public GaacService(final SyncEntityRepository<Gaac> repository,
                            final EntityToModelMapper<Gaac, GaacModel> entityToModelMapper,
                            final ModelToEntityMapper<GaacModel, Gaac> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.GAAC;
    }
}
