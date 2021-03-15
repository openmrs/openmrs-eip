package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.OrderLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderLightRepository extends OpenmrsRepository<OrderLight> {

    @Override
    @Cacheable(cacheNames = "order", unless="#result == null")
    OrderLight findByUuid(String uuid);
}
