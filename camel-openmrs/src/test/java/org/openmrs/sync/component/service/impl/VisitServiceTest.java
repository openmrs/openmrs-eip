package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Visit;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.VisitModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class VisitServiceTest {

    @Mock
    private SyncEntityRepository<Visit> repository;

    @Mock
    private EntityToModelMapper<Visit, VisitModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<VisitModel, Visit> modelToEntityMapper;

    private VisitService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.VISIT, service.getTableToSync());
    }
}
