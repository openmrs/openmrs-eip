package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterLightRepository extends OpenmrsRepository<EncounterLight> {

    @Override
    @Cacheable(cacheNames = "encounter", unless="#result == null")
    EncounterLight findByUuid(String uuid);
}
