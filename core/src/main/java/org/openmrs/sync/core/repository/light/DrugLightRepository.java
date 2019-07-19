package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.DrugLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface DrugLightRepository extends OpenMrsRepository<DrugLight> {

    @Override
    @Cacheable(cacheNames = "drug", unless="#result == null")
    DrugLight findByUuid(String uuid);
}
