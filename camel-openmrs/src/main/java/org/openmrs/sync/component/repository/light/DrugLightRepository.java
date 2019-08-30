package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.DrugLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface DrugLightRepository extends OpenMrsRepository<DrugLight> {

    @Override
    @Cacheable(cacheNames = "drug", unless="#result == null")
    DrugLight findByUuid(String uuid);
}
