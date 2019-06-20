package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowStateContext;
import org.springframework.stereotype.Service;

@Service
public class ProgramWorkflowStateLightService extends AbstractLightService<ProgramWorkflowStateLight, ProgramWorkflowStateContext> {

    private LightService<ConceptLight, ConceptContext> conceptService;

    private LightService<ProgramWorkflowLight, ProgramWorkflowContext> programWorkflowService;

    public ProgramWorkflowStateLightService(final OpenMrsRepository<ProgramWorkflowStateLight> repository,
                                            final LightService<ConceptLight, ConceptContext> conceptService,
                                            final LightService<ProgramWorkflowLight, ProgramWorkflowContext> programWorkflowService) {
        super(repository);
        this.conceptService = conceptService;
        this.programWorkflowService = programWorkflowService;
    }

    @Override
    protected ProgramWorkflowStateLight getShadowEntity(final String uuid, final ProgramWorkflowStateContext context) {
        ProgramWorkflowStateLight workflowState = new ProgramWorkflowStateLight();
        workflowState.setUuid(uuid);
        workflowState.setDateCreated(DEFAULT_DATE);
        workflowState.setCreator(DEFAULT_USER_ID);
        workflowState.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));
        workflowState.setProgramWorkflow(programWorkflowService.getOrInit(context.getWorkflowUuid(), getWorkflowContext(context)));

        return workflowState;
    }

    private ProgramWorkflowContext getWorkflowContext(final ProgramWorkflowStateContext context) {
        return ProgramWorkflowContext.builder()
                .conceptUuid(context.getWorkflowConceptUuid())
                .conceptClassUuid(context.getWorkflowConceptClassUuid())
                .conceptDatatypeUuid(context.getWorkflowConceptDatatypeUuid())
                .programUuid(context.getWorkflowProgramUuid())
                .programConceptUuid(context.getWorkflowProgramConceptUuid())
                .programConceptClassUuid(context.getWorkflowProgramConceptClassUuid())
                .programConceptDatatypeUuid(context.getWorkflowProgramConceptDatatypeUuid())
                .build();
    }

    private ConceptContext getConceptContext(final ProgramWorkflowStateContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
