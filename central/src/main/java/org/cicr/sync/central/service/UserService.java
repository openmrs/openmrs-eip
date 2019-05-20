package org.cicr.sync.central.service;

import org.cicr.sync.central.repository.UserRepository;
import org.cicr.sync.core.entity.UserEty;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEty getOrInitUser(final String uuid) {
        UserEty user = userRepository.findByUuid(uuid);

        if (user == null) {
            user = new UserEty();
            user.setUuid(uuid);
            user.setCreator(1);
            user.setDateCreated("1970-01-01 00:00:00");
            user.setSystemId("admin");
            user.setPersonId(1);
        }

        return user;
    }
}
