package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.VisitAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.VisitAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class VisitAttributeService extends AbstractEntityService<VisitAttribute, VisitAttributeModel> {

    public VisitAttributeService(final SyncEntityRepository<VisitAttribute> repository,
                                 final EntityToModelMapper<VisitAttribute, VisitAttributeModel> entityToModelMapper,
                                 final ModelToEntityMapper<VisitAttributeModel, VisitAttribute> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.VISIT_ATTRIBUTE;
    }
}
