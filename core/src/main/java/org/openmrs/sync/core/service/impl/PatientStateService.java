package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientStateService extends AbstractEntityService<PatientState, PatientStateModel> {

    public PatientStateService(final SyncEntityRepository<PatientState> repository,
                               final EntityMapper<PatientState, PatientStateModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PATIENT_STATE;
    }
}
