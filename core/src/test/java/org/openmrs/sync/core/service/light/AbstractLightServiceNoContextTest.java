package org.openmrs.sync.core.service.light;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AbstractLightServiceNoContextTest {

    @Mock
    private OpenMrsRepository<MockedLightEntity> repository;

    private MockedLightServiceNoContext service;

    private static final String UUID = "uuid";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new MockedLightServiceNoContext(repository);
    }

    @Test
    public void getOrInit_exists() {
        // Given
        MockedLightEntity userEty = new MockedLightEntity(1L, UUID);
        when(repository.findByUuid(UUID)).thenReturn(userEty);
        when(repository.save(userEty)).thenReturn(userEty);

        // When
        MockedLightEntity result = service.getOrInit(UUID);

        // Then
        verify(repository, never()).save(any());
        assertEquals(userEty, result);
    }

    @Test
    public void getOrInit_create() {
        // Given
        when(repository.findByUuid(UUID)).thenReturn(null);
        when(repository.save(new MockedLightEntity(null, UUID))).thenReturn(getExpectedEntity());

        // When
        MockedLightEntity result = service.getOrInit(UUID);

        // Then
        assertEquals(getExpectedEntity(), result);
    }

    private MockedLightEntity getExpectedEntity() {
        return new MockedLightEntity(1L, UUID);
    }
}
