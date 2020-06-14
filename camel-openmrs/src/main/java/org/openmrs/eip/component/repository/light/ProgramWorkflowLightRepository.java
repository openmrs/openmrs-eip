package org.openmrs.eip.component.repository.light;

import org.openmrs.eip.component.entity.light.ProgramWorkflowLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramWorkflowLightRepository extends OpenmrsRepository<ProgramWorkflowLight> {

    @Override
    @Cacheable(cacheNames = "programWorkflow", unless="#result == null")
    ProgramWorkflowLight findByUuid(String uuid);
}
