package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.model.PersonModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PersonServiceTest {

    @Mock
    private SyncEntityRepository<Person> repository;

    @Mock
    private EntityToModelMapper<Person, PersonModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PersonModel, Person> modelToEntityMapper;

    private PersonService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PERSON, service.getTableToSync());
    }

    @Test
    public void mapEntities() {
        // Given
        Person person = new Person();
        Patient patient = new Patient();
        PersonModel personModel = new PersonModel();
        PatientModel patientModel = new PatientModel();
        when(entityToModelMapper.apply(person)).thenReturn(personModel);
        when(entityToModelMapper.apply(patient)).thenReturn(patientModel);

        // When
        List<PersonModel> result = service.mapEntities(Arrays.asList(person, patient));

        // Then
        assertEquals(1, result.size());
        verify(entityToModelMapper, never()).apply(patient);
        assertTrue(result.get(0) instanceof PersonModel);
    }
}
