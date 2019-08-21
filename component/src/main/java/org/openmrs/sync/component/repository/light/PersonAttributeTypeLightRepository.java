package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PersonAttributeTypeLightRepository extends OpenMrsRepository<PersonAttributeTypeLight> {

    @Override
    @Cacheable(cacheNames = "personAttributeType", unless="#result == null")
    PersonAttributeTypeLight findByUuid(String uuid);
}
