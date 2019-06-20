package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PatientProgram;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PatientProgramModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientProgramService extends AbstractEntityService<PatientProgram, PatientProgramModel> {

    public PatientProgramService(final SyncEntityRepository<PatientProgram> repository,
                                 final EntityMapper<PatientProgram, PatientProgramModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.PATIENT_PROGRAM;
    }
}
