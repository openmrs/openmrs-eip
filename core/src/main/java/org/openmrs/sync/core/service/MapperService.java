package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;

public interface MapperService<E extends BaseEntity, M extends BaseModel> {

    Class<M> getCorrespondingModelClass(E entity);

    Class<E> getCorrespondingEntityClass(M model);
}
