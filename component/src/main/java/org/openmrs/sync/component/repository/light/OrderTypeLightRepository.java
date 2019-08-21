package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderTypeLightRepository extends OpenMrsRepository<OrderTypeLight> {

    @Override
    @Cacheable(cacheNames = "orderType", unless="#result == null")
    OrderTypeLight findByUuid(String uuid);
}
