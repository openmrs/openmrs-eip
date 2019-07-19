package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface LocationLightRepository extends OpenMrsRepository<LocationLight> {

    @Override
    @Cacheable(cacheNames = "location", unless="#result == null")
    LocationLight findByUuid(String uuid);
}
