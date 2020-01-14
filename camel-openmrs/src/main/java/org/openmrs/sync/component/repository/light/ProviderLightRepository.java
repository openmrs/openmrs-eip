package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ProviderLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProviderLightRepository extends OpenmrsRepository<ProviderLight> {

    @Override
    @Cacheable(cacheNames = "provider", unless="#result == null")
    ProviderLight findByUuid(String uuid);
}
