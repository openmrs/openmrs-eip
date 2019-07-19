package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ObservationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ObservationLightRepository extends OpenMrsRepository<ObservationLight> {

    @Override
    @Cacheable(cacheNames = "observation", unless="#result == null")
    ObservationLight findByUuid(String uuid);
}
