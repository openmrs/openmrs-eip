package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.CareSettingLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface CareSettingLightRepository extends OpenMrsRepository<CareSettingLight> {

    @Override
    @Cacheable(cacheNames = "careSetting", unless="#result == null")
    CareSettingLight findByUuid(String uuid);
}
