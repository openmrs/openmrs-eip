package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptAttributeTypeLightRepository extends OpenmrsRepository<ConceptAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "conceptAttributeType", unless="#result == null")
    ConceptAttributeTypeLight findByUuid(String uuid);
}
