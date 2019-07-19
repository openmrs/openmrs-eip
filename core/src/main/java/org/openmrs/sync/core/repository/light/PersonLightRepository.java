package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PersonLightRepository extends OpenMrsRepository<PersonLight> {

    @Override
    @Cacheable(cacheNames = "person", unless="#result == null")
    PersonLight findByUuid(String uuid);
}
