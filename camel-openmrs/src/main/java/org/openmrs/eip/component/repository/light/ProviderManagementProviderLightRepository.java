package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ProviderManagementProviderLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProviderManagementProviderLightRepository extends OpenmrsRepository<ProviderManagementProviderLight> {

    @Override
    @Cacheable(cacheNames = "providerManagementProviderType", unless="#result == null")
    ProviderManagementProviderLight findByUuid(String uuid);
}
