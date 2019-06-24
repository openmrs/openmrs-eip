package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ConceptAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.ConceptAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.EntityNameEnum;

import static org.junit.Assert.assertEquals;

public class ConceptAttributeServiceTest {

    @Mock
    private SyncEntityRepository<ConceptAttribute> repository;

    @Mock
    private EntityMapper<ConceptAttribute, ConceptAttributeModel> mapper;

    private ConceptAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptAttributeService(repository, mapper);
    }

    @Test
    public void getEntityName() {
        assertEquals(EntityNameEnum.CONCEPT_ATTRIBUTE, service.getEntityName());
    }
}
