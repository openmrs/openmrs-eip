package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface PersonAttributeTypeLightRepository extends OpenmrsRepository<PersonAttributeTypeLight> {
	
	@Override
	@Cacheable(cacheNames = "personAttributeType", unless = "#result == null")
	PersonAttributeTypeLight findByUuid(String uuid);
}
