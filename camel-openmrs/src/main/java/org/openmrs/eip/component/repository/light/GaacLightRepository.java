package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacFamilyLite;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacLightRepository extends OpenmrsRepository<GaacFamilyLite> {

    @Override
    @Cacheable(cacheNames = "gaacFamily", unless="#result == null")
    GaacFamilyLite findByUuid(String uuid);
}
