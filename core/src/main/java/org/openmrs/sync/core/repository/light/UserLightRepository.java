package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface UserLightRepository extends OpenMrsRepository<UserLight> {

    @Override
    @Cacheable(cacheNames = "user", unless="#result == null")
    UserLight findByUuid(String uuid);
}
