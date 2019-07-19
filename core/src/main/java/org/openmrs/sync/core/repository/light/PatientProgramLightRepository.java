package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientProgramLightRepository extends OpenMrsRepository<PatientProgramLight> {

    @Override
    @Cacheable(cacheNames = "patientProgram", unless="#result == null")
    PatientProgramLight findByUuid(String uuid);
}
