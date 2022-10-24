package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptLightRepository extends OpenmrsRepository<ConceptLight> {
	
	@Override
	@Cacheable(cacheNames = "concept", unless = "#result == null")
	ConceptLight findByUuid(String uuid);
}
