package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ConceptDataTypeLightServiceTest {

    @Mock
    private OpenMrsRepository<ConceptDatatypeLight> repository;

    private ConceptDatatypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptDatatypeLightService(repository);
    }

    @Test
    public void getShadowEntity() {
        // Given

        // When
        ConceptDatatypeLight result = service.getShadowEntity("UUID");

        // Then
        assertEquals(getExpectedConceptDatatype(), result);
    }

    private ConceptDatatypeLight getExpectedConceptDatatype() {
        ConceptDatatypeLight conceptDatatype = new ConceptDatatypeLight();
        conceptDatatype.setUuid("UUID");
        conceptDatatype.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        conceptDatatype.setCreator(1L);
        conceptDatatype.setName("[Default]");
        return conceptDatatype;
    }
}
