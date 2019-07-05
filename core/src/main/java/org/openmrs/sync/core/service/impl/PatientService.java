package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientService extends AbstractEntityService<Patient, PatientModel> {

    public PatientService(final SyncEntityRepository<Patient> personRepository,
                          final EntityToModelMapper<Patient, PatientModel> entityToModelMapper,
                          final ModelToEntityMapper<PatientModel, Patient> modelToEntityMapper) {
        super(personRepository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PATIENT;
    }
}
