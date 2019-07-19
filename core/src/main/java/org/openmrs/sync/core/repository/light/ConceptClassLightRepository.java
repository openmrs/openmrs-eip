package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptClassLightRepository extends OpenMrsRepository<ConceptClassLight> {

    @Override
    @Cacheable(cacheNames = "conceptClass", unless="#result == null")
    ConceptClassLight findByUuid(String uuid);
}
