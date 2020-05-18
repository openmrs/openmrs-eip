package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.OrderGroupLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderGroupLightRepository extends OpenmrsRepository<OrderGroupLight> {

    @Override
    @Cacheable(cacheNames = "orderGroup", unless = "#result == null")
    OrderGroupLight findByUuid(String uuid);

}
