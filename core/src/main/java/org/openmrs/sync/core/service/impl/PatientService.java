package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientService extends AbstractEntityService<Patient, PatientModel> {

    public PatientService(final SyncEntityRepository<Patient> personRepository,
                          final EntityMapper<Patient, PatientModel> mapper) {
        super(personRepository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PATIENT;
    }
}
