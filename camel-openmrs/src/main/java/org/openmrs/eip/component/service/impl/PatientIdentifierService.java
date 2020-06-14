package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.PatientIdentifier;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
