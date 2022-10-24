package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.EncounterDiagnosis;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.EncounterDiagnosisModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
