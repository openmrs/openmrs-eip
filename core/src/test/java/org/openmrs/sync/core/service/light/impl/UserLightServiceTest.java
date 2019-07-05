package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class UserLightServiceTest {

    @Mock
    private OpenMrsRepository<UserLight> repository;

    private UserLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new UserLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        UserLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedUser(), result);
    }

    private UserLight getExpectedUser() {
        UserLight user = new UserLight();
        user.setCreator(1L);
        user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        user.setSystemId("admin");
        user.setPersonId(1L);
        return user;
    }
}
