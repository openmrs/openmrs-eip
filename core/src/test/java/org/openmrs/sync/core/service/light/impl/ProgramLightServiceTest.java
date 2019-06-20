package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProgramLightServiceTest {

    @Mock
    private OpenMrsRepository<ProgramLight> repository;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    private ProgramLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProgramLightService(repository, conceptService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        ProgramContext programContext = ProgramContext.builder()
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
        ProgramLight result = service.getShadowEntity("UUID", programContext);

        // Then
        assertEquals(getExpectedPatientProgram(), result);
    }

    private ProgramLight getExpectedPatientProgram() {
        ProgramLight program = new ProgramLight();
        program.setUuid("UUID");
        program.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        program.setCreator(1L);
        program.setName("[Default]");
        program.setConcept(getConcept());
        return program;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
