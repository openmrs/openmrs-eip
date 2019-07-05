package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.VisitAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.model.VisitAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

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
