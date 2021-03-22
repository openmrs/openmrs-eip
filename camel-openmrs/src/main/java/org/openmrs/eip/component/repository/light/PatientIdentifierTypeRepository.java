package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientIdentifierTypeRepository extends OpenmrsRepository<PatientIdentifierTypeLight> {

    @Override
    @Cacheable(cacheNames = "patientIdentifierType", unless="#result == null")
    PatientIdentifierTypeLight findByUuid(String uuid);
}
