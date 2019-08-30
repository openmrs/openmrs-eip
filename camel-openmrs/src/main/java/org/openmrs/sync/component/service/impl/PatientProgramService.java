package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PatientProgram;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PatientProgramModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
