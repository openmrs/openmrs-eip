package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ProgramLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramLightRepository extends OpenMrsRepository<ProgramLight> {

    @Override
    @Cacheable(cacheNames = "program", unless="#result == null")
    ProgramLight findByUuid(String uuid);
}
