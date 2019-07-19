package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface LocationAttributeTypeLightRepository extends OpenMrsRepository<LocationAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "locationAttributeType", unless="#result == null")
    LocationAttributeTypeLight findByUuid(String uuid);
}
