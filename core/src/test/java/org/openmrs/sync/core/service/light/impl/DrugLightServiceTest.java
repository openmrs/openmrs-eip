package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.DrugLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.DrugContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class DrugLightServiceTest {

    @Mock
    private OpenMrsRepository<DrugLight> repository;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    private DrugLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new DrugLightService(repository, conceptService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        DrugContext drugContext = DrugContext.builder()
                .conceptUuid("concept")
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(getConcept());

        // When
        DrugLight result = service.getShadowEntity("UUID", drugContext);

        // Then
        assertEquals(getExpectedDrug(), result);
    }

    private DrugLight getExpectedDrug() {
        DrugLight drug = new DrugLight();
        drug.setUuid("UUID");
        drug.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        drug.setCreator(1L);
        drug.setConcept(getConcept());
        return drug;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
