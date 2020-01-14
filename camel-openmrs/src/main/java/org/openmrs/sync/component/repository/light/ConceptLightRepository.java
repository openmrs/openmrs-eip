package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConceptLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptLightRepository extends OpenmrsRepository<ConceptLight> {

    @Override
    @Cacheable(cacheNames = "concept", unless="#result == null")
    ConceptLight findByUuid(String uuid);
}
