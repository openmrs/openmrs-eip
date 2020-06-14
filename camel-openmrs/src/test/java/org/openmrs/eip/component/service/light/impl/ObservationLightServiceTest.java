package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ObservationLight;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ObservationLightServiceTest {

    @Mock
    private OpenmrsRepository<ObservationLight> repository;

    @Mock
    private LightService<PersonLight> personService;

    @Mock
    private LightService<ConceptLight> conceptService;

    private ObservationLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ObservationLightService(repository, personService, conceptService);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        when(conceptService.getOrInitPlaceholderEntity()).thenReturn(getConcept());
        when(personService.getOrInitPlaceholderEntity()).thenReturn(getPerson());
        String uuid = "uuid";

        // When
        ObservationLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedObservation(), result);
    }

    private ObservationLight getExpectedObservation() {
        ObservationLight observation = new ObservationLight();
        observation.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        observation.setCreator(1L);
        observation.setObsDatetime(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        observation.setConcept(getConcept());
        observation.setPerson(getPerson());
        return observation;
    }

    private PersonLight getPerson() {
        PersonLight person = new PersonLight();
        person.setUuid("PLACEHOLDER_PERSON");
        return person;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("PLACEHOLDER_CONCEPT");
        return concept;
    }
}
