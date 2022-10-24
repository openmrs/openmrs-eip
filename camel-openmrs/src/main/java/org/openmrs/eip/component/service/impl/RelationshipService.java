package org.openmrs.eip.component.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.eip.component.entity.Relationship;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.RelationshipModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class RelationshipService extends AbstractEntityService<Relationship, RelationshipModel> {
	
	public RelationshipService(final SyncEntityRepository<Relationship> relationshipRepository,
	    final EntityToModelMapper<Relationship, RelationshipModel> entityToModelMapper,
	    final ModelToEntityMapper<RelationshipModel, Relationship> modelToEntityMapper) {
		super(relationshipRepository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.RELATIONSHIP;
	}
	
	@Override
	protected List<RelationshipModel> mapEntities(final List<Relationship> entities) {
		return entities.stream().filter(relationship -> !(relationship instanceof Relationship)).map(entityToModelMapper)
		        .collect(Collectors.toList());
	}
}
