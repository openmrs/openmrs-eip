package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.GaacFamilyLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface GaacFamilyLightRepository extends OpenmrsRepository<GaacFamilyLight> {
	
	@Override
	@Cacheable(cacheNames = "gaacFamily", unless = "#result == null")
	GaacFamilyLight findByUuid(String uuid);
}
