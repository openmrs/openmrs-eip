package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.RelationshipLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface RelationshipLightRepository extends OpenmrsRepository<RelationshipLight> {

    @Override
    @Cacheable(cacheNames = "relationship", unless="#result == null")
    RelationshipLight findByUuid(String uuid);
}
