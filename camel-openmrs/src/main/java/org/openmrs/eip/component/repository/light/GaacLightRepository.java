package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacLightRepository extends OpenmrsRepository<GaacLight> {
	
	@Override
	@Cacheable(cacheNames = "gaac", unless = "#result == null")
	GaacLight findByUuid(String uuid);
}
