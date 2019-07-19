package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptAttributeTypeLightRepository extends OpenMrsRepository<ConceptAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "conceptAttributeType", unless="#result == null")
    ConceptAttributeTypeLight findByUuid(String uuid);
}
