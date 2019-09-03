package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConditionLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConditionLightRepository extends OpenMrsRepository<ConditionLight> {

    @Override
    @Cacheable(cacheNames = "condition", unless="#result == null")
    ConditionLight findByUuid(String uuid);
}
