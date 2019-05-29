package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class UserService extends AbstractSimpleService<UserLight> {

    public UserService(final OpenMrsRepository<UserLight> userRepository) {
        super(userRepository);
    }

    @Override
    protected UserLight getFakeEntity(final String uuid) {
        UserLight user = new UserLight();
        user.setUuid(uuid);
        user.setCreator(1L);
        user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        user.setSystemId("admin");
        user.setPersonId(1);
        return user;
    }
}
