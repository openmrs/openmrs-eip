package org.openmrs.sync.core.service.light;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.MockedAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class AbstractAttributeTypeLightServiceTest {

    @Mock
    private OpenMrsRepository<MockedAttributeTypeLight> repository;

    private MockedAttributeTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new MockedAttributeTypeLightService(repository);
    }

    @Test
    public void getShadowEntity() {
        // Given


        // When
        MockedAttributeTypeLight result = service.getShadowEntity("UUID");

        // Then
        assertEquals(getExpectedAttributeType(), result);
    }

    private MockedAttributeTypeLight getExpectedAttributeType() {
        MockedAttributeTypeLight attributeType = new MockedAttributeTypeLight(null, "UUID");
        attributeType.setName("[Default]");
        attributeType.setMinOccurs(0);
        attributeType.setCreator(1L);
        attributeType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return attributeType;
    }
}
