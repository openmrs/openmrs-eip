package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitAttributeTypeLightRepository extends OpenmrsRepository<VisitAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "visitAttributeType", unless="#result == null")
    VisitAttributeTypeLight findByUuid(String uuid);
}
