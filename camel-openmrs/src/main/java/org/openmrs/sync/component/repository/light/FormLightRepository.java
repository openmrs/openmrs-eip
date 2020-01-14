package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.FormLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface FormLightRepository extends OpenmrsRepository<FormLight> {

    @Override
    @Cacheable(cacheNames = "form", unless="#result == null")
    FormLight findByUuid(String uuid);
}
