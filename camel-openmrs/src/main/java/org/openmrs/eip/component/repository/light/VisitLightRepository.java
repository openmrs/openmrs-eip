package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.VisitLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitLightRepository extends OpenmrsRepository<VisitLight> {

    @Override
    @Cacheable(cacheNames = "visit", unless="#result == null")
    VisitLight findByUuid(String uuid);
}
