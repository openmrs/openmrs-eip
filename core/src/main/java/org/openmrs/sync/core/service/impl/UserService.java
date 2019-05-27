package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.User;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class UserService extends AbstractSimpleService<User> {

    public UserService(final OpenMrsRepository<User> userRepository) {
        super(userRepository);
    }

    @Override
    protected User getFakeEntity(final String uuid) {
        User user = new User();
        user.setUuid(uuid);
        user.setCreator(1);
        user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        user.setSystemId("admin");
        user.setPersonId(1);
        return user;
    }
}
