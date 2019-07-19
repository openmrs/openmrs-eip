package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptDatatypeLightRepository extends OpenMrsRepository<ConceptDatatypeLight> {

    @Override
    @Cacheable(cacheNames = "conceptDataType", unless="#result == null")
    ConceptDatatypeLight findByUuid(String uuid);
}
