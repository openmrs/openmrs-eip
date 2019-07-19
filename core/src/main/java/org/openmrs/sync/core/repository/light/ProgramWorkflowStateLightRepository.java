package org.openmrs.sync.core.repository.light;

import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.cache.annotation.Cacheable;

public interface ProgramWorkflowStateLightRepository extends OpenMrsRepository<ProgramWorkflowStateLight> {

    @Override
    @Cacheable(cacheNames = "programWorkflowState", unless="#result == null")
    ProgramWorkflowStateLight findByUuid(String uuid);
}
