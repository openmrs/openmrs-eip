package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.EncounterTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterTypeLightRepository extends OpenMrsRepository<EncounterTypeLight> {

    @Override
    @Cacheable(cacheNames = "encounterType", unless="#result == null")
    EncounterTypeLight findByUuid(String uuid);
}
