package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.repository.AuditableRepository;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PatientService extends AbstractEntityService<Patient, PatientModel> {

    public PatientService(final AuditableRepository<Patient> repository,
                          final Function<Patient, PatientModel> entityToModelMapper,
                          final Function<PatientModel, Patient> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableNameEnum getTableName() {
        return TableNameEnum.PATIENT;
    }
}
