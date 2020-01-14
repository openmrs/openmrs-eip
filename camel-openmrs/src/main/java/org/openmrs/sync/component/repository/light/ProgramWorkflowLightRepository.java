package org.openmrs.sync.component.repository.light;

import org.openmrs.sync.component.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramWorkflowLightRepository extends OpenmrsRepository<ProgramWorkflowLight> {

    @Override
    @Cacheable(cacheNames = "programWorkflow", unless="#result == null")
    ProgramWorkflowLight findByUuid(String uuid);
}
