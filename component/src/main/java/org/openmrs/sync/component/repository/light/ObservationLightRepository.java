package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ObservationLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ObservationLightRepository extends OpenMrsRepository<ObservationLight> {

    @Override
    @Cacheable(cacheNames = "observation", unless="#result == null")
    ObservationLight findByUuid(String uuid);
}
