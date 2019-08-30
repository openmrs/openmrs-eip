package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.PersonAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.AttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PersonAttributeServiceTest {

    @Mock
    private SyncEntityRepository<PersonAttribute> repository;

    @Mock
    private EntityToModelMapper<PersonAttribute, AttributeModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<AttributeModel, PersonAttribute> modelToEntityMapper;

    private PersonAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonAttributeService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PERSON_ATTRIBUTE, service.getTableToSync());
    }
}
