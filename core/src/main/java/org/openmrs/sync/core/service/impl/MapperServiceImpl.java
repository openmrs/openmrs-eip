package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.MapperService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {

    @Override
    public Class<? extends BaseModel> getCorrespondingModelClass(final BaseEntity entity) {
        return TableToSyncEnum.getModelClass(entity);
    }
}
