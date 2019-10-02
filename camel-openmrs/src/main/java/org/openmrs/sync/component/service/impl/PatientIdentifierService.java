package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PatientIdentifier;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PatientIdentifierModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientIdentifierService extends AbstractEntityService<PatientIdentifier, PatientIdentifierModel> {

    public PatientIdentifierService(final SyncEntityRepository<PatientIdentifier> repository,
                                    final EntityToModelMapper<PatientIdentifier, PatientIdentifierModel> entityToModelMapper,
                                    final ModelToEntityMapper<PatientIdentifierModel, PatientIdentifier> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PATIENT_IDENTIFIER;
    }
}
