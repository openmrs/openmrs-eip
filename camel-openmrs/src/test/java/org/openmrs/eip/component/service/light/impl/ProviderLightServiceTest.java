package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.ProviderLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ProviderLightServiceTest {

    @Mock
    private OpenmrsRepository<ProviderLight> repository;

    private ProviderLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProviderLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        ProviderLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedProvider(), result);
    }

    private ProviderLight getExpectedProvider() {
        ProviderLight provider = new ProviderLight();
        provider.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        provider.setCreator(1L);
        return provider;
    }
}
