package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderTypeLightRepository extends OpenmrsRepository<OrderTypeLight> {

    @Override
    @Cacheable(cacheNames = "orderType", unless="#result == null")
    OrderTypeLight findByUuid(String uuid);
}
