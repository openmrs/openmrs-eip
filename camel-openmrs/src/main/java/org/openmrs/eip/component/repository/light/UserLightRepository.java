package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface UserLightRepository extends OpenmrsRepository<UserLight> {

    @Override
    @Cacheable(cacheNames = "user", unless="#result == null")
    UserLight findByUuid(String uuid);
}
