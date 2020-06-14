package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.ConceptAttributeModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.ConceptAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

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
        Assert.assertEquals(TableToSyncEnum.CONCEPT_ATTRIBUTE, service.getTableToSync());
    }
}
