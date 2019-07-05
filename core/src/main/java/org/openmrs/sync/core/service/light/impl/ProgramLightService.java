package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class ProgramLightService extends AbstractLightService<ProgramLight> {

    private LightService<ConceptLight> conceptService;

    public ProgramLightService(final OpenMrsRepository<ProgramLight> repository,
                               final LightService<ConceptLight> conceptService) {
        super(repository);
        this.conceptService = conceptService;
    }

    @Override
    protected ProgramLight createPlaceholderEntity(final String uuid) {
        ProgramLight program = new ProgramLight();
        program.setDateCreated(DEFAULT_DATE);
        program.setCreator(DEFAULT_USER_ID);
        program.setName(DEFAULT_STRING);
        program.setConcept(conceptService.getOrInitPlaceholderEntity());

        return program;
    }
}
