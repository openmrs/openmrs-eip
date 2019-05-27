package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.User;
import org.openmrs.sync.core.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    @Mock
    private UserRepository repository;

    private UserService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new UserService(repository);
    }

    @Test
    public void getFakeEntity() {
        assertEquals(getExpectedUser(), service.getFakeEntity("uuid"));
    }

    private User getExpectedUser() {
        User user = new User();
        user.setUuid("uuid");
        user.setCreator(1);
        user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        user.setSystemId("admin");
        user.setPersonId(1);
        return user;
    }
}
