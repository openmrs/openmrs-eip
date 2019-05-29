package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.repository.AuditableRepository;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Component;

@Component
public class PatientService extends AbstractEntityService<Patient, PatientModel> {

    public PatientService(final AuditableRepository<Patient> repository,
                          final EntityMapper<Patient, PatientModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableNameEnum getTableName() {
        return TableNameEnum.PATIENT;
    }
}
