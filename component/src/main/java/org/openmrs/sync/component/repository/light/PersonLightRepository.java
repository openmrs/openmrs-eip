package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.PersonLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PersonLightRepository extends OpenMrsRepository<PersonLight> {

    @Override
    @Cacheable(cacheNames = "person", unless="#result == null")
    PersonLight findByUuid(String uuid);
}
