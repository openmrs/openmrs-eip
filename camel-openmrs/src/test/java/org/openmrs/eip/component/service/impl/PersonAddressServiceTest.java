package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.PersonAddress;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

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
        Assert.assertEquals(TableToSyncEnum.PERSON_ADDRESS, service.getTableToSync());
    }
}
