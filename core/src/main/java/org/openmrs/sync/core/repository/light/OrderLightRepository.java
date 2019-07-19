package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.OrderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderLightRepository extends OpenMrsRepository<OrderLight> {

    @Override
    @Cacheable(cacheNames = "order", unless="#result == null")
    OrderLight findByUuid(String uuid);
}
