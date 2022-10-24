package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.ObservationModel;
import org.openmrs.eip.component.entity.Observation;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ObservationService extends AbstractEntityService<Observation, ObservationModel> {
	
	public ObservationService(final SyncEntityRepository<Observation> repository,
	    final EntityToModelMapper<Observation, ObservationModel> entityToModelMapper,
	    final ModelToEntityMapper<ObservationModel, Observation> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.OBS;
	}
}
