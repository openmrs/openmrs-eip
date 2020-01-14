package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.PatientProgramLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientProgramLightRepository extends OpenmrsRepository<PatientProgramLight> {

    @Override
    @Cacheable(cacheNames = "patientProgram", unless="#result == null")
    PatientProgramLight findByUuid(String uuid);
}
