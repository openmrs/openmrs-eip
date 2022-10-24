package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.PatientState;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PatientStateModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientStateService extends AbstractEntityService<PatientState, PatientStateModel> {
	
	public PatientStateService(final SyncEntityRepository<PatientState> repository,
	    final EntityToModelMapper<PatientState, PatientStateModel> entityToModelMapper,
	    final ModelToEntityMapper<PatientStateModel, PatientState> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.PATIENT_STATE;
	}
}
