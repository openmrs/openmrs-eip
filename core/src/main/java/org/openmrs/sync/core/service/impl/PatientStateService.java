package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientStateService extends AbstractEntityService<PatientState, PatientStateModel> {

    public PatientStateService(final SyncEntityRepository<PatientState> repository,
                               final EntityToModelMapper<PatientState, PatientStateModel> entityToModelMapper,
                               final ModelToEntityMapper<PatientStateModel, PatientState> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PATIENT_STATE;
    }
}
