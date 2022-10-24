package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.PatientProgramLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientProgramLightRepository extends OpenmrsRepository<PatientProgramLight> {
	
	@Override
	@Cacheable(cacheNames = "patientProgram", unless = "#result == null")
	PatientProgramLight findByUuid(String uuid);
}
