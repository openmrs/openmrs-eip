package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.Patient;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.TableToSyncEnum;

public class PatientServiceTest {

    @Mock
    private SyncEntityRepository<Patient> repository;

    @Mock
    private SyncEntityRepository<Person> personRepo;

    @Mock
    private EntityToModelMapper<Patient, PatientModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PatientModel, Patient> modelToEntityMapper;

    private PatientService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientService(repository, personRepo, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        Assert.assertEquals(TableToSyncEnum.PATIENT, service.getTableToSync());
    }
}
