package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PersonAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PersonAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PersonAttributeServiceTest {

    @Mock
    private SyncEntityRepository<PersonAttribute> repository;

    @Mock
    private EntityMapper<PersonAttribute, PersonAttributeModel> mapper;

    private PersonAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonAttributeService(repository, mapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PERSON_ATTRIBUTE, service.getTableToSync());
    }
}
