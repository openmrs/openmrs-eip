package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.common.model.sync.PatientModel;
import org.openmrs.sync.component.entity.Patient;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
