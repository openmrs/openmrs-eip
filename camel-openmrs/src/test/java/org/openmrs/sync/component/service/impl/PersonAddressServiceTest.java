package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.PersonAddress;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PersonAddressModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PersonAddressServiceTest {

    @Mock
    private SyncEntityRepository<PersonAddress> repository;

    @Mock
    private EntityToModelMapper<PersonAddress, PersonAddressModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PersonAddressModel, PersonAddress> modelToEntityMapper;

    private PersonAddressService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonAddressService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PERSON_ADDRESS, service.getTableToSync());
    }
}
