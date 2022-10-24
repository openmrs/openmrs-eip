package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.GaacFamily;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.GaacFamilyModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class GaacFamilyService extends AbstractEntityService<GaacFamily, GaacFamilyModel> {
	
	public GaacFamilyService(final SyncEntityRepository<GaacFamily> repository,
	    final EntityToModelMapper<GaacFamily, GaacFamilyModel> entityToModelMapper,
	    final ModelToEntityMapper<GaacFamilyModel, GaacFamily> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.GAAC_FAMILY;
	}
}
