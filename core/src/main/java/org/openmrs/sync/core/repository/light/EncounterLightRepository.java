package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.EncounterLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterLightRepository extends OpenMrsRepository<EncounterLight> {

    @Override
    @Cacheable(cacheNames = "encounter", unless="#result == null")
    EncounterLight findByUuid(String uuid);
}
