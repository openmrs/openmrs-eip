package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderLightRepository extends OpenmrsRepository<OrderLight> {

    @Override
    @Cacheable(cacheNames = "order", unless="#result == null")
    OrderLight findByUuid(String uuid);
}
