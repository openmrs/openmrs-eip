package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface LocationAttributeTypeLightRepository extends OpenMrsRepository<LocationAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "locationAttributeType", unless="#result == null")
    LocationAttributeTypeLight findByUuid(String uuid);
}
