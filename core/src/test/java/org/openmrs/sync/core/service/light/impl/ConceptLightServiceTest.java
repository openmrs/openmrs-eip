package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConceptLightServiceTest {

    @Mock
    private OpenMrsRepository<ConceptLight> repository;

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
    public void getShadowEntity() {
        // Given
        when(conceptClassService.getOrInit("conceptClassUuid")).thenReturn(getConceptClass());
        when(conceptDatatypeService.getOrInit("conceptDatatypeUuid")).thenReturn(getConceptDatatype());
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptDatatypeUuid("conceptDatatypeUuid")
                .conceptClassUuid("conceptClassUuid")
                .build();

        // When
        ConceptLight result = service.getShadowEntity("UUID", conceptContext);

        // Then
        assertEquals(getExpectedConcept(), result);
    }

    private ConceptLight getExpectedConcept() {
        ConceptClassLight conceptClass = getConceptClass();

        ConceptDatatypeLight conceptDatatype = getConceptDatatype();

        ConceptLight expected = new ConceptLight();
        expected.setUuid("UUID");
        expected.setConceptClass(conceptClass);
        expected.setDatatype(conceptDatatype);
        expected.setCreator(1L);
        expected.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return expected;
    }

    private ConceptClassLight getConceptClass() {
        ConceptClassLight conceptClass = new ConceptClassLight();
        conceptClass.setUuid("conceptClassUuid");
        return conceptClass;
    }

    private ConceptDatatypeLight getConceptDatatype() {
        ConceptDatatypeLight conceptDatatype = new ConceptDatatypeLight();
        conceptDatatype.setUuid("conceptDatatypeUuid");
        return conceptDatatype;
    }
}
