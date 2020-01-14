package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PatientIdentifierTypeRepository extends OpenmrsRepository<PatientIdentifierTypeLight> {

    @Override
    @Cacheable(cacheNames = "patientIdentifierType", unless="#result == null")
    PatientIdentifierTypeLight findByUuid(String uuid);
}
