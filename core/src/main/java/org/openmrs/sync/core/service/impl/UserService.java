package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.UserEty;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class UserService extends AbstractSimpleService<UserEty> {

    private OpenMrsRepository<UserEty> userRepository;

    public UserService(final OpenMrsRepository<UserEty> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected OpenMrsRepository<UserEty> getRepository() {
        return userRepository;
    }

    @Override
    protected UserEty getFakeEntity(final String uuid) {
        UserEty user = new UserEty();
        user.setUuid(uuid);
        user.setCreator(1);
        user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        user.setSystemId("admin");
        user.setPersonId(1);
        return user;
    }
}
