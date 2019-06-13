package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ObservationLight;
import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ObservationContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ObservationLightServiceTest {

    @Mock
    private OpenMrsRepository<ObservationLight> repository;

    @Mock
    private LightServiceNoContext<PersonLight> personService;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    private ObservationLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ObservationLightService(repository, personService, conceptService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        ObservationContext observationContext = ObservationContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .conceptUuid("concept")
                .personUuid("person")
                .build();
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(getConcept());
        when(personService.getOrInit("person")).thenReturn(getPerson());

        // When
        ObservationLight result = service.getShadowEntity("UUID", observationContext);

        // Then
        assertEquals(getExpectedObservation(), result);
    }

    private ObservationLight getExpectedObservation() {
        ObservationLight observation = new ObservationLight();
        observation.setUuid("UUID");
        observation.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        observation.setCreator(1L);
        observation.setObsDatetime(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        observation.setConcept(getConcept());
        observation.setPerson(getPerson());
        return observation;
    }

    private PersonLight getPerson() {
        PersonLight person = new PersonLight();
        person.setUuid("person");
        return person;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
