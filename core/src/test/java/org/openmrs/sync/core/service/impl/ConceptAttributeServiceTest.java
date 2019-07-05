package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ConceptAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.ConceptAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ConceptAttributeServiceTest {

    @Mock
    private SyncEntityRepository<ConceptAttribute> repository;

    @Mock
    private EntityToModelMapper<ConceptAttribute, ConceptAttributeModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<ConceptAttributeModel, ConceptAttribute> modelToEntityMapper;

    private ConceptAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptAttributeService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.CONCEPT_ATTRIBUTE, service.getTableToSync());
    }
}
