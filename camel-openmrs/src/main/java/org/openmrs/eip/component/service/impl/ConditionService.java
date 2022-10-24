package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.ConditionModel;
import org.openmrs.eip.component.entity.Condition;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConditionService extends AbstractEntityService<Condition, ConditionModel> {
	
	public ConditionService(final SyncEntityRepository<Condition> repository,
	    final EntityToModelMapper<Condition, ConditionModel> entityToModelMapper,
	    final ModelToEntityMapper<ConditionModel, Condition> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.CONDITION;
	}
}
