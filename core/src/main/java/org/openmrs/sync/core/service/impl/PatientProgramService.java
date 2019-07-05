package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PatientProgram;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientProgramModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
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
