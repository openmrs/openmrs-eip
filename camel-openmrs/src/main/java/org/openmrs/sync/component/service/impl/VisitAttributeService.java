package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.VisitAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.VisitAttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
