package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.RelationshipTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface RelationshipTypeLightRepository extends OpenmrsRepository<RelationshipTypeLight> {
	
	@Override
	@Cacheable(cacheNames = "relationshipTypeLight", unless = "#result == null")
	RelationshipTypeLight findByUuid(String uuid);
}
