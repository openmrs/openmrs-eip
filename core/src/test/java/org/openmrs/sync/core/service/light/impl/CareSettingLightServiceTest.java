package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.CareSettingLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class CareSettingLightServiceTest {

    @Mock
    private OpenMrsRepository<CareSettingLight> repository;

    private CareSettingLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new CareSettingLightService(repository);
    }

    @Test
    public void getShadowEntity() {
        // Given

        // When
        CareSettingLight result = service.getShadowEntity("UUID");

        // Then
        assertEquals(getExpectedCareSetting(), result);
    }

    private CareSettingLight getExpectedCareSetting() {
        CareSettingLight careSetting = new CareSettingLight();
        careSetting.setUuid("UUID");
        careSetting.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        careSetting.setCreator(1L);
        careSetting.setCareSettingType("[Default]");
        careSetting.setName("[Default]");
        return careSetting;
    }
}
