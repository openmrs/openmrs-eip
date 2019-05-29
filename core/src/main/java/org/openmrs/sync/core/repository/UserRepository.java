package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.light.UserLight;

public interface UserRepository extends OpenMrsRepository<UserLight> {

    @Override
    UserLight findByUuid(final String uuid);
}
