package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowStateLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class ProgramWorkflowStateLightService extends AbstractLightService<ProgramWorkflowStateLight> {

    private LightService<ConceptLight> conceptService;

    private LightService<ProgramWorkflowLight> programWorkflowService;

    public ProgramWorkflowStateLightService(final OpenmrsRepository<ProgramWorkflowStateLight> repository,
                                            final LightService<ConceptLight> conceptService,
                                            final LightService<ProgramWorkflowLight> programWorkflowService) {
        super(repository);
        this.conceptService = conceptService;
        this.programWorkflowService = programWorkflowService;
    }

    @Override
    protected ProgramWorkflowStateLight createPlaceholderEntity(final String uuid) {
        ProgramWorkflowStateLight workflowState = new ProgramWorkflowStateLight();
        workflowState.setDateCreated(DEFAULT_DATE);
        workflowState.setCreator(DEFAULT_USER_ID);
        workflowState.setConcept(conceptService.getOrInitPlaceholderEntity());
        workflowState.setProgramWorkflow(programWorkflowService.getOrInitPlaceholderEntity());

        return workflowState;
    }
}
