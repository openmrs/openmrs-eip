package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.UserEty;

public interface UserRepository extends OpenMrsRepository<UserEty> {

    /**
     * find user by uuid
     * @param uuid the uuid
     * @return UserEty
     */
    @Override
    UserEty findByUuid(final String uuid);
}
