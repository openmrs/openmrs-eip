package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.ConceptModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ConceptServiceTest {

    @Mock
    private SyncEntityRepository<Concept> repository;

    @Mock
    private EntityToModelMapper<Concept, ConceptModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<ConceptModel, Concept> modelToEntityMapper;

    private ConceptService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.CONCEPT, service.getTableToSync());
    }
}
