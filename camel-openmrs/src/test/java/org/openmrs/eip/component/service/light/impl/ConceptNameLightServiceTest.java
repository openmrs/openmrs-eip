package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ConceptNameLightServiceTest {

    @Mock
    private OpenmrsRepository<ConceptNameLight> repository;

    private ConceptNameLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptNameLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        ConceptNameLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedConceptName(), result);
    }

    private ConceptNameLight getExpectedConceptName() {
        ConceptNameLight conceptName = new ConceptNameLight();
        conceptName.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        conceptName.setCreator(1L);
        conceptName.setName("[Default]");
        conceptName.setLocale("en");
        return conceptName;
    }
}
