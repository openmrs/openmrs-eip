package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.EntityNameEnum;
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
    private EntityMapper<Person, PersonModel> mapper;

    private PersonService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonService(repository, mapper);
    }

    @Test
    public void getEntityName() {
        assertEquals(EntityNameEnum.PERSON, service.getEntityName());
    }

    @Test
    public void mapEntities() {
        // Given
        Person person = new Person();
        Patient patient = new Patient();
        PersonModel personModel = new PersonModel();
        PatientModel patientModel = new PatientModel();
        when(mapper.entityToModel(person)).thenReturn(personModel);
        when(mapper.entityToModel(patient)).thenReturn(patientModel);

        // When
        List<PersonModel> result = service.mapEntities(Arrays.asList(person, patient));

        // Then
        assertEquals(1, result.size());
        verify(mapper, never()).entityToModel(patient);
        assertTrue(result.get(0) instanceof PersonModel);
    }
}
