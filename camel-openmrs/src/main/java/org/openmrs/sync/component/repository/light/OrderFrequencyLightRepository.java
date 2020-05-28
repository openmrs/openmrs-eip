package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderFrequencyLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderFrequencyLightRepository extends OpenmrsRepository<OrderFrequencyLight> {

    @Override
    @Cacheable(cacheNames = "orderFrequency", unless = "#result == null")
    OrderFrequencyLight findByUuid(String uuid);

}
