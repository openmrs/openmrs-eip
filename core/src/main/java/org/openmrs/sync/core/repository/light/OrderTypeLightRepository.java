package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.OrderTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderTypeLightRepository extends OpenMrsRepository<OrderTypeLight> {

    @Override
    @Cacheable(cacheNames = "orderType", unless="#result == null")
    OrderTypeLight findByUuid(String uuid);
}
