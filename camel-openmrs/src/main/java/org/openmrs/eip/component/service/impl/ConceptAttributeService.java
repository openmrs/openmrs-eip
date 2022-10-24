package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.ConceptAttributeModel;
import org.openmrs.eip.component.entity.ConceptAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeService extends AbstractEntityService<ConceptAttribute, ConceptAttributeModel> {
	
	public ConceptAttributeService(final SyncEntityRepository<ConceptAttribute> repository,
	    final EntityToModelMapper<ConceptAttribute, ConceptAttributeModel> entityToModelMapper,
	    final ModelToEntityMapper<ConceptAttributeModel, ConceptAttribute> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.CONCEPT_ATTRIBUTE;
	}
}
