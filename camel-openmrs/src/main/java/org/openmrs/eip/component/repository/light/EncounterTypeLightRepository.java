package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterTypeLightRepository extends OpenmrsRepository<EncounterTypeLight> {

    @Override
    @Cacheable(cacheNames = "encounterType", unless="#result == null")
    EncounterTypeLight findByUuid(String uuid);
}
