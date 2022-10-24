package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.VisitAttributeModel;
import org.openmrs.eip.component.entity.VisitAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class VisitAttributeService extends AbstractEntityService<VisitAttribute, VisitAttributeModel> {
	
	public VisitAttributeService(final SyncEntityRepository<VisitAttribute> repository,
	    final EntityToModelMapper<VisitAttribute, VisitAttributeModel> entityToModelMapper,
	    final ModelToEntityMapper<VisitAttributeModel, VisitAttribute> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.VISIT_ATTRIBUTE;
	}
}
