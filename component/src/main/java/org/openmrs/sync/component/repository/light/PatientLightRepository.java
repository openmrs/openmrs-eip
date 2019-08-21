package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.PatientLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientLightRepository extends OpenMrsRepository<PatientLight> {

    @Override
    @Cacheable(cacheNames = "patient", unless="#result == null")
    PatientLight findByUuid(String uuid);
}
