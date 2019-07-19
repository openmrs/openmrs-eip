package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitLightRepository extends OpenMrsRepository<VisitLight> {

    @Override
    @Cacheable(cacheNames = "visit", unless="#result == null")
    VisitLight findByUuid(String uuid);
}
