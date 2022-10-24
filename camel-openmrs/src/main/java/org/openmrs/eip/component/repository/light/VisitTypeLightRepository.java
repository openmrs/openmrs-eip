package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.VisitTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface VisitTypeLightRepository extends OpenmrsRepository<VisitTypeLight> {
	
	@Override
	@Cacheable(cacheNames = "visitType", unless = "#result == null")
	VisitTypeLight findByUuid(String uuid);
}
