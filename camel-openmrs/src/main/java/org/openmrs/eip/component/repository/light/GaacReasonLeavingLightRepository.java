package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacReasonLeavingTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacReasonLeavingLightRepository extends OpenmrsRepository<GaacReasonLeavingTypeLight> {

    @Override
    @Cacheable(cacheNames = "gaacReasonLeavingType", unless="#result == null")
    GaacReasonLeavingTypeLight findByUuid(String uuid);
}
