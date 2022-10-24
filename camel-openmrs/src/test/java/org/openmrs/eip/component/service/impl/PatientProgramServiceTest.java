package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.PatientProgramModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.PatientProgram;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class PatientProgramServiceTest {
	
	@Mock
	private SyncEntityRepository<PatientProgram> repository;
	
	@Mock
	private EntityToModelMapper<PatientProgram, PatientProgramModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<PatientProgramModel, PatientProgram> modelToEntityMapper;
	
	private PatientProgramService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new PatientProgramService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.PATIENT_PROGRAM, service.getTableToSync());
	}
}
