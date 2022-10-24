package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ProgramWorkflowStateLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramWorkflowStateLightRepository extends OpenmrsRepository<ProgramWorkflowStateLight> {
	
	@Override
	@Cacheable(cacheNames = "programWorkflowState", unless = "#result == null")
	ProgramWorkflowStateLight findByUuid(String uuid);
}
