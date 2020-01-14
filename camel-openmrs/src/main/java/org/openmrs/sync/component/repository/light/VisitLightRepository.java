package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.VisitLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitLightRepository extends OpenmrsRepository<VisitLight> {

    @Override
    @Cacheable(cacheNames = "visit", unless="#result == null")
    VisitLight findByUuid(String uuid);
}
