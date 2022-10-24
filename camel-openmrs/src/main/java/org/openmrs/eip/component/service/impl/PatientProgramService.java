package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.PatientProgram;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PatientProgramModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientProgramService extends AbstractEntityService<PatientProgram, PatientProgramModel> {
	
	public PatientProgramService(final SyncEntityRepository<PatientProgram> repository,
	    final EntityToModelMapper<PatientProgram, PatientProgramModel> entityToModelMapper,
	    final ModelToEntityMapper<PatientProgramModel, PatientProgram> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.PATIENT_PROGRAM;
	}
}
