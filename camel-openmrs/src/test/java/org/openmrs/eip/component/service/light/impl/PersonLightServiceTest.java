package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PersonLightServiceTest {

    @Mock
    private OpenmrsRepository<PersonLight> repository;

    private PersonLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        PersonLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedPerson(), result);
    }

    private PersonLight getExpectedPerson() {
        PersonLight person = new PersonLight();
        person.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return person;
    }
}
