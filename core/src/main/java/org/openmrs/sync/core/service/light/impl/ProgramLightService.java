package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;
import org.springframework.stereotype.Service;

@Service
public class ProgramLightService extends AbstractLightService<ProgramLight, ProgramContext> {

    private LightService<ConceptLight, ConceptContext> conceptService;

    public ProgramLightService(final OpenMrsRepository<ProgramLight> repository,
                               final LightService<ConceptLight, ConceptContext> conceptService) {
        super(repository);
        this.conceptService = conceptService;
    }

    @Override
    public ProgramLight getShadowEntity(final String uuid, final ProgramContext context) {
        ProgramLight program = new ProgramLight();
        program.setUuid(uuid);
        program.setDateCreated(DEFAULT_DATE);
        program.setCreator(DEFAULT_USER_ID);
        program.setName(DEFAULT_STRING);
        program.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));

        return program;
    }

    private ConceptContext getConceptContext(final ProgramContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
