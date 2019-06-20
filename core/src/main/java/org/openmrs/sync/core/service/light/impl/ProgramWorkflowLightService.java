package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowContext;
import org.springframework.stereotype.Service;

@Service
public class ProgramWorkflowLightService extends AbstractLightService<ProgramWorkflowLight, ProgramWorkflowContext> {

    private LightService<ConceptLight, ConceptContext> conceptService;

    private LightService<ProgramLight, ProgramContext> programService;

    public ProgramWorkflowLightService(final OpenMrsRepository<ProgramWorkflowLight> repository,
                                       final LightService<ConceptLight, ConceptContext> conceptService,
                                       final LightService<ProgramLight, ProgramContext> programService) {
        super(repository);
        this.conceptService = conceptService;
        this.programService = programService;
    }

    @Override
    protected ProgramWorkflowLight getShadowEntity(final String uuid, final ProgramWorkflowContext context) {
        ProgramWorkflowLight programWorkflow = new ProgramWorkflowLight();
        programWorkflow.setUuid(uuid);
        programWorkflow.setDateCreated(DEFAULT_DATE);
        programWorkflow.setCreator(DEFAULT_USER_ID);
        programWorkflow.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));
        programWorkflow.setProgram(programService.getOrInit(context.getProgramUuid(), getProgramContext(context)));

        return programWorkflow;
    }

    private ProgramContext getProgramContext(final ProgramWorkflowContext context) {
        return ProgramContext.builder()
                .conceptUuid(context.getProgramConceptUuid())
                .conceptClassUuid(context.getProgramConceptClassUuid())
                .conceptDatatypeUuid(context.getProgramConceptDatatypeUuid())
                .build();
    }

    private ConceptContext getConceptContext(final ProgramWorkflowContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
