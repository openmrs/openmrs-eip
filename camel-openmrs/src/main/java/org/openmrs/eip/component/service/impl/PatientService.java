package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Patient;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PatientService extends AbstractSubclassEntityService<Person, Patient, PatientModel> {
	
	public PatientService(final SyncEntityRepository<Patient> patientRepository,
	    SyncEntityRepository<Person> personRepository, final EntityToModelMapper<Patient, PatientModel> entityToModelMapper,
	    final ModelToEntityMapper<PatientModel, Patient> modelToEntityMapper) {
		
		super(patientRepository, personRepository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.PATIENT;
	}
	
}
