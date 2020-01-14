package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ConditionLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConditionLightRepository extends OpenmrsRepository<ConditionLight> {

    @Override
    @Cacheable(cacheNames = "condition", unless="#result == null")
    ConditionLight findByUuid(String uuid);
}
