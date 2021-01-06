package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacLite;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacLightRepository extends OpenmrsRepository<GaacLite> {

    @Override
    @Cacheable(cacheNames = "gaac", unless="#result == null")
    GaacLite findByUuid(String uuid);
}
