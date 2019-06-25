package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.VisitAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.VisitAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class VisitAttributeService extends AbstractEntityService<VisitAttribute, VisitAttributeModel> {

    public VisitAttributeService(final SyncEntityRepository<VisitAttribute> repository,
                                 final EntityMapper<VisitAttribute, VisitAttributeModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.VISIT_ATTRIBUTE;
    }
}
