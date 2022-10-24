package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacAffinityTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacAffinityTypeLightRepository extends OpenmrsRepository<GaacAffinityTypeLight> {
	
	@Override
	@Cacheable(cacheNames = "gaacAffinityType", unless = "#result == null")
	GaacAffinityTypeLight findByUuid(String uuid);
}
