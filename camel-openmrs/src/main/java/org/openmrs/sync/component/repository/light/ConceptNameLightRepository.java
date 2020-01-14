package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConceptNameLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptNameLightRepository extends OpenmrsRepository<ConceptNameLight> {

    @Override
    @Cacheable(cacheNames = "conceptName", unless="#result == null")
    ConceptNameLight findByUuid(String uuid);
}
