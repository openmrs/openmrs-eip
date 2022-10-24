package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.EncounterModel;
import org.openmrs.eip.component.entity.Encounter;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class EncounterService extends AbstractEntityService<Encounter, EncounterModel> {
	
	public EncounterService(final SyncEntityRepository<Encounter> repository,
	    final EntityToModelMapper<Encounter, EncounterModel> entityToModelMapper,
	    final ModelToEntityMapper<EncounterModel, Encounter> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.ENCOUNTER;
	}
}
