package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ConceptClassLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptClassLightRepository extends OpenmrsRepository<ConceptClassLight> {
	
	@Override
	@Cacheable(cacheNames = "conceptClass", unless = "#result == null")
	ConceptClassLight findByUuid(String uuid);
}
