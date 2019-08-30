package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderLightRepository extends OpenMrsRepository<OrderLight> {

    @Override
    @Cacheable(cacheNames = "order", unless="#result == null")
    OrderLight findByUuid(String uuid);
}
