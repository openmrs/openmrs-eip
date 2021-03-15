package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.EncounterRoleLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface EncounterRoleLightRepository extends OpenmrsRepository<EncounterRoleLight> {

    @Override
    @Cacheable(cacheNames = "encounterRoleType", unless="#result == null")
    EncounterRoleLight findByUuid(String uuid);
}
