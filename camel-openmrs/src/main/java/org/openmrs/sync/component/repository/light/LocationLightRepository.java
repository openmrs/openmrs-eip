package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.LocationLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface LocationLightRepository extends OpenmrsRepository<LocationLight> {

    @Override
    @Cacheable(cacheNames = "location", unless="#result == null")
    LocationLight findByUuid(String uuid);
}
