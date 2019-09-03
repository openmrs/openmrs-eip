package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Allergy;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.AllergyModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class AllergyServiceTest {

    @Mock
    private SyncEntityRepository<Allergy> repository;

    @Mock
    private EntityToModelMapper<Allergy, AllergyModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<AllergyModel, Allergy> modelToEntityMapper;

    private AllergyService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new AllergyService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.ALLERGY, service.getTableToSync());
    }
}
