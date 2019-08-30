package org.openmrs.sync.component.service;

import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.model.BaseModel;

public interface MapperService<E extends BaseEntity, M extends BaseModel> {

    Class<M> getCorrespondingModelClass(E entity);

    Class<E> getCorrespondingEntityClass(M model);
}
