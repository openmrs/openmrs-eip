package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PersonAttributeTypeLightRepository extends OpenMrsRepository<PersonAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "personAttributeType", unless="#result == null")
    PersonAttributeTypeLight findByUuid(String uuid);
}
