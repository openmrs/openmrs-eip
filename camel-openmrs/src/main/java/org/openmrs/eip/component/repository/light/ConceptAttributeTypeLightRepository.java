package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ConceptAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ConceptAttributeTypeLightRepository extends OpenmrsRepository<ConceptAttributeTypeLight> {
	
	@Override
	@Cacheable(cacheNames = "conceptAttributeType", unless = "#result == null")
	ConceptAttributeTypeLight findByUuid(String uuid);
}
