package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.light.impl.context.MockedContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AbstractLightServiceTest {

    @Mock
    private OpenMrsRepository<MockedLightEntity> repository;

    private MockedLightService service;

    private static final String UUID = "uuid";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new MockedLightService(repository);
    }

    @Test
    public void getOrInit_exists() {
        // Given
        MockedLightEntity userEty = new MockedLightEntity(1L, UUID);
        when(repository.findByUuid(UUID)).thenReturn(userEty);
        when(repository.save(userEty)).thenReturn(userEty);

        // When
        MockedLightEntity result = service.getOrInit(UUID, new MockedContext());

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
        MockedLightEntity result = service.getOrInit(UUID, new MockedContext());

        // Then
        assertEquals(getExpectedEntity(), result);
    }

    private MockedLightEntity getExpectedEntity() {
        return new MockedLightEntity(1L, UUID);
    }
}
