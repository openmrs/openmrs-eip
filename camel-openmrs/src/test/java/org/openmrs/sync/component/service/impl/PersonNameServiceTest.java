package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.PersonName;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PersonNameModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

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
        assertEquals(TableToSyncEnum.PERSON_NAME, service.getTableToSync());
    }
}
