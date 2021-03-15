package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptNameLightRepository extends OpenmrsRepository<ConceptNameLight> {

    @Override
    @Cacheable(cacheNames = "conceptName", unless="#result == null")
    ConceptNameLight findByUuid(String uuid);
}
