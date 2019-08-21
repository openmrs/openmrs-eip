package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.UserLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface UserLightRepository extends OpenMrsRepository<UserLight> {

    @Override
    @Cacheable(cacheNames = "user", unless="#result == null")
    UserLight findByUuid(String uuid);
}
