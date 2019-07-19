package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.EncounterTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterTypeLightRepository extends OpenMrsRepository<EncounterTypeLight> {

    @Override
    @Cacheable(cacheNames = "encounterType", unless="#result == null")
    EncounterTypeLight findByUuid(String uuid);
}
