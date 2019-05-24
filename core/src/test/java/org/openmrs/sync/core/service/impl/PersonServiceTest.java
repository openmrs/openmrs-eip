package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.core.entity.PersonEty;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class PersonServiceTest {

    @Mock
    private OpenMrsRepository<PersonEty> personRepository;

    @Mock
    private Function<PersonEty, PersonModel> etyToModelMapper;

    @Mock
    private Function<PersonModel, PersonEty> modelToEtyMapper;

    private PersonService personService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        personService = new PersonService(personRepository, etyToModelMapper, modelToEtyMapper);
    }

    @Test
    public void getEntityName() {
        assertEquals(TableNameEnum.PERSON, personService.getEntityName());
    }
}
