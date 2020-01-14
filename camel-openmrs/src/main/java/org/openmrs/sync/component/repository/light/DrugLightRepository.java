package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.DrugLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface DrugLightRepository extends OpenmrsRepository<DrugLight> {

    @Override
    @Cacheable(cacheNames = "drug", unless="#result == null")
    DrugLight findByUuid(String uuid);
}
