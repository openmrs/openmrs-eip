package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.Visit;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

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
        Assert.assertEquals(TableToSyncEnum.VISIT, service.getTableToSync());
    }
}
