package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacReasonLeavingLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacReasonLeavingLightRepository extends OpenmrsRepository<GaacReasonLeavingLight> {

    @Override
    @Cacheable(cacheNames = "gaacReasonLeavingType", unless="#result == null")
    GaacReasonLeavingLight findByUuid(String uuid);
}
