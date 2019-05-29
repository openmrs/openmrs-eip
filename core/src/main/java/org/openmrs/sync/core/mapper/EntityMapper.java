package org.openmrs.sync.core.mapper;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;

public interface EntityMapper<E extends BaseEntity, M extends BaseModel> {

    M entityToModel(E entity);

    E modelToEntity(M model);
}
