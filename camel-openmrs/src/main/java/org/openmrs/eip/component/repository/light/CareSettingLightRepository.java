package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface CareSettingLightRepository extends OpenmrsRepository<CareSettingLight> {

    @Override
    @Cacheable(cacheNames = "careSetting", unless="#result == null")
    CareSettingLight findByUuid(String uuid);
}
