package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramWorkflowLightRepository extends OpenMrsRepository<ProgramWorkflowLight> {

    @Override
    @Cacheable(cacheNames = "programWorkflow", unless="#result == null")
    ProgramWorkflowLight findByUuid(String uuid);
}
