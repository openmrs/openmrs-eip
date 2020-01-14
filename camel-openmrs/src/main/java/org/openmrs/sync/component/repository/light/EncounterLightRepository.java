package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.EncounterLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterLightRepository extends OpenmrsRepository<EncounterLight> {

    @Override
    @Cacheable(cacheNames = "encounter", unless="#result == null")
    EncounterLight findByUuid(String uuid);
}
