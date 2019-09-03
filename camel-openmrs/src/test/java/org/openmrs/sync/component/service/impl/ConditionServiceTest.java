package org.openmrs.sync.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Condition;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.ConditionModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.impl.ConditionService;

import static org.junit.Assert.assertEquals;

public class ConditionServiceTest {

    @Mock
    private SyncEntityRepository<Condition> repository;

    @Mock
    private EntityToModelMapper<Condition, ConditionModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<ConditionModel, Condition> modelToEntityMapper;

    private ConditionService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConditionService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        Assert.assertEquals(TableToSyncEnum.CONDITION, service.getTableToSync());
    }
}
