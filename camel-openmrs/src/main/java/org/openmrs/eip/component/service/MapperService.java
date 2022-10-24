package org.openmrs.eip.component.service;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.entity.BaseEntity;

public interface MapperService<E extends BaseEntity, M extends BaseModel> {
	
	Class<M> getCorrespondingModelClass(E entity);
	
	Class<E> getCorrespondingEntityClass(M model);
}
