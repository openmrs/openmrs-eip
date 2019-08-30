package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.VisitAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.VisitAttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class VisitAttributeServiceTest {

    @Mock
    private SyncEntityRepository<VisitAttribute> repository;

    @Mock
    private EntityToModelMapper<VisitAttribute, VisitAttributeModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<VisitAttributeModel, VisitAttribute> modelToEntityMapper;

    private VisitAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitAttributeService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.VISIT_ATTRIBUTE, service.getTableToSync());
    }
}
