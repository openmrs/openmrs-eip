package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.entity.light.OrderFrequencyLight;
import org.springframework.cache.annotation.Cacheable;

public interface OrderFrequencyLightRepository extends OpenmrsRepository<OrderFrequencyLight> {

    @Override
    @Cacheable(cacheNames = "orderFrequency", unless = "#result == null")
    OrderFrequencyLight findByUuid(String uuid);

}
