package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.repository.AuditableRepository;
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
    private AuditableRepository<Person> personRepository;

    @Mock
    private EntityMapper<Person, PersonModel> mapper;

    private PersonService personService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        personService = new PersonService(personRepository, mapper);
    }

    @Test
    public void getEntityName() {
        assertEquals(EntityNameEnum.PERSON, personService.getEntityName());
    }
}
