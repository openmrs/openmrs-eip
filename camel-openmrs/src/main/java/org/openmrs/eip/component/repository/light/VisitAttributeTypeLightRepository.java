package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitAttributeTypeLightRepository extends OpenmrsRepository<VisitAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "visitAttributeType", unless="#result == null")
    VisitAttributeTypeLight findByUuid(String uuid);
}
