package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;

public interface MapperService {

    Class<? extends BaseModel> getCorrespondingModelClass(BaseEntity entity);
}
