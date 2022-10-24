package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;

public abstract class AbstractSubclassEntityService<P extends BaseEntity, E extends P, M extends BaseModel> extends AbstractEntityService<E, M> {
	
	//Repository class for the parent entity
	private SyncEntityRepository<P> parentRepository;
	
	public AbstractSubclassEntityService(final SyncEntityRepository<E> repository, SyncEntityRepository<P> parentRepository,
	    final EntityToModelMapper<E, M> entityToModelMapper, final ModelToEntityMapper<M, E> modelToEntityMapper) {
		
		super(repository, entityToModelMapper, modelToEntityMapper);
		this.parentRepository = parentRepository;
	}
	
	/**
	 * Gets the parentRepository
	 *
	 * @return the parentRepository
	 */
	public SyncEntityRepository<P> getParentRepository() {
		return parentRepository;
	}
	
}
