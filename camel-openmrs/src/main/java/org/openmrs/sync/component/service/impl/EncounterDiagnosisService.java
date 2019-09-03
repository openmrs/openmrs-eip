package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.EncounterDiagnosis;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.EncounterDiagnosisModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class EncounterDiagnosisService extends AbstractEntityService<EncounterDiagnosis, EncounterDiagnosisModel> {

    public EncounterDiagnosisService(final SyncEntityRepository<EncounterDiagnosis> repository,
                                     final EntityToModelMapper<EncounterDiagnosis, EncounterDiagnosisModel> entityToModelMapper,
                                     final ModelToEntityMapper<EncounterDiagnosisModel, EncounterDiagnosis> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ENCOUNTER_DIAGNOSIS;
    }
}
