package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

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
