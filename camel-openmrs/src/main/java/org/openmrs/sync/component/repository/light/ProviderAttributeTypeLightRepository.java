package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProviderAttributeTypeLightRepository extends OpenmrsRepository<ProviderAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "providerAttributeType", unless="#result == null")
    ProviderAttributeTypeLight findByUuid(String uuid);
}
