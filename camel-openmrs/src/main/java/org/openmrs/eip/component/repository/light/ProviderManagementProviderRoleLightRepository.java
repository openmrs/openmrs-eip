package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ProviderManagementProviderRoleLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProviderManagementProviderRoleLightRepository extends OpenmrsRepository<ProviderManagementProviderRoleLight> {
	
	@Override
	@Cacheable(cacheNames = "providerManagementProviderType", unless = "#result == null")
	ProviderManagementProviderRoleLight findByUuid(String uuid);
}
