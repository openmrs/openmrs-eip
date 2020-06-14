package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientLightRepository extends OpenmrsRepository<PatientLight> {

    @Override
    @Cacheable(cacheNames = "patient", unless="#result == null")
    PatientLight findByUuid(String uuid);
}
