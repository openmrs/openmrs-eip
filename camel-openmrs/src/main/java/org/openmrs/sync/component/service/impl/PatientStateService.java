package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PatientState;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PatientStateModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
