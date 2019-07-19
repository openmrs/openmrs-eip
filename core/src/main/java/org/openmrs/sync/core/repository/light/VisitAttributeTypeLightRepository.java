package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitAttributeTypeLightRepository extends OpenMrsRepository<VisitAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "visitAttributeType", unless="#result == null")
    VisitAttributeTypeLight findByUuid(String uuid);
}
