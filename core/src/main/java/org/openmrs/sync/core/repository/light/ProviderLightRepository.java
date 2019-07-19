package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProviderLightRepository extends OpenMrsRepository<ProviderLight> {

    @Override
    @Cacheable(cacheNames = "provider", unless="#result == null")
    ProviderLight findByUuid(String uuid);
}
