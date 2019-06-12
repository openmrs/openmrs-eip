package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.EntityNameEnum;

import static org.junit.Assert.assertEquals;

public class VisitServiceTest {

    @Mock
    private SyncEntityRepository<Visit> repository;

    @Mock
    private EntityMapper<Visit, VisitModel> mapper;

    private VisitService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitService(repository, mapper);
    }

    @Test
    public void getEntityName() {
        assertEquals(EntityNameEnum.VISIT, service.getEntityName());
    }
}
