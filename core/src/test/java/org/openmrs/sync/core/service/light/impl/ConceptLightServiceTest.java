package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.light.ConceptLightRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConceptLightServiceTest {

    @Mock
    private ConceptLightRepository repository;

    @Mock
    private ConceptClassLightService conceptClassService;

    @Mock
    private ConceptDatatypeLightService conceptDatatypeService;

    private ConceptLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptLightService(repository, conceptClassService, conceptDatatypeService);
    }

    @Test
    public void getFakeEntity() {
        // Given
        AttributeUuid conceptClassUuid = AttributeHelper.buildConceptClassAttributeUuid("conceptClassUuid");
        AttributeUuid conceptDatatypeUuid = AttributeHelper.buildConceptDatatypeAttributeUuid("conceptDatatypeUuid");
        when(conceptClassService.getOrInit("conceptClassUuid")).thenReturn(getConceptClassLight());
        when(conceptDatatypeService.getOrInit("conceptDatatypeUuid")).thenReturn(getConceptDatatypeLight());

        // When
        ConceptLight result = service.getFakeEntity("UUID", Arrays.asList(conceptClassUuid, conceptDatatypeUuid));

        // Then
        assertEquals(getExpectedConcept(), result);
    }

    private ConceptLight getExpectedConcept() {
        ConceptClassLight conceptClass = getConceptClassLight();

        ConceptDatatypeLight conceptDatatype = getConceptDatatypeLight();

        ConceptLight expected = new ConceptLight();
        expected.setUuid("UUID");
        expected.setConceptClass(conceptClass);
        expected.setDatatype(conceptDatatype);
        expected.setCreator(1L);
        expected.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return expected;
    }

    private ConceptClassLight getConceptClassLight() {
        ConceptClassLight conceptClass = new ConceptClassLight();
        conceptClass.setUuid("conceptClassUuid");
        return conceptClass;
    }

    private ConceptDatatypeLight getConceptDatatypeLight() {
        ConceptDatatypeLight conceptDatatype = new ConceptDatatypeLight();
        conceptDatatype.setUuid("conceptDatatypeUuid");
        return conceptDatatype;
    }
}
