package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.PersonName;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class PersonNameServiceTest {

    @Mock
    private SyncEntityRepository<PersonName> repository;

    @Mock
    private EntityToModelMapper<PersonName, PersonNameModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PersonNameModel, PersonName> modelToEntityMapper;

    private PersonNameService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonNameService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        Assert.assertEquals(TableToSyncEnum.PERSON_NAME, service.getTableToSync());
    }
}
