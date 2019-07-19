package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptLightRepository extends OpenMrsRepository<ConceptLight> {

    @Override
    @Cacheable(cacheNames = "concept", unless="#result == null")
    ConceptLight findByUuid(String uuid);
}
