package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ProgramLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramLightRepository extends OpenmrsRepository<ProgramLight> {

    @Override
    @Cacheable(cacheNames = "program", unless="#result == null")
    ProgramLight findByUuid(String uuid);
}
