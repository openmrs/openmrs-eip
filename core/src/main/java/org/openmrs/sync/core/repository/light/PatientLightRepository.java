package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientLightRepository extends OpenMrsRepository<PatientLight> {

    @Override
    @Cacheable(cacheNames = "patient", unless="#result == null")
    PatientLight findByUuid(String uuid);
}
