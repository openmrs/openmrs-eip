package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.model.PersonModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

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
}
