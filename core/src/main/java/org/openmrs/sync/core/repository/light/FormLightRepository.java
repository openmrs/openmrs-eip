package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.FormLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface FormLightRepository extends OpenMrsRepository<FormLight> {

    @Override
    @Cacheable(cacheNames = "form", unless="#result == null")
    FormLight findByUuid(String uuid);
}
