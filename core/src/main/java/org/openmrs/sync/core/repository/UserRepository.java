package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.User;

public interface UserRepository extends OpenMrsRepository<User> {

    @Override
    User findByUuid(final String uuid);
}
