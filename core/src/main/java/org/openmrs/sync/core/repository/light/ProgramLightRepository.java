package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramLightRepository extends OpenMrsRepository<ProgramLight> {

    @Override
    @Cacheable(cacheNames = "program", unless="#result == null")
    ProgramLight findByUuid(String uuid);
}
