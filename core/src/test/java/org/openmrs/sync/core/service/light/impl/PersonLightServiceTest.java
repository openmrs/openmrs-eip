package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PersonLightServiceTest {

    @Mock
    private OpenMrsRepository<PersonLight> repository;

    private PersonLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PersonLightService(repository);
    }

    @Test
    public void getShadowEntity() {
        // Given

        // When
        PersonLight result = service.getShadowEntity("UUID");

        // Then
        assertEquals(getExpectedPerson(), result);
    }

    private PersonLight getExpectedPerson() {
        PersonLight person = new PersonLight();
        person.setUuid("UUID");
        person.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return person;
    }
}
