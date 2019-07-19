package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitTypeLightRepository extends OpenMrsRepository<VisitTypeLight> {

    @Override
    @Cacheable(cacheNames = "visitType", unless="#result == null")
    VisitTypeLight findByUuid(String uuid);
}
