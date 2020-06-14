package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.OrderGroupLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface OrderGroupLightRepository extends OpenmrsRepository<OrderGroupLight> {

    @Override
    @Cacheable(cacheNames = "orderGroup", unless = "#result == null")
    OrderGroupLight findByUuid(String uuid);

}
