package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptDatatypeLightRepository extends OpenmrsRepository<ConceptDatatypeLight> {

    @Override
    @Cacheable(cacheNames = "conceptDataType", unless="#result == null")
    ConceptDatatypeLight findByUuid(String uuid);
}
