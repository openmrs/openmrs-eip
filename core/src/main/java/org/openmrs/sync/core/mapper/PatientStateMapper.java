package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.PatientProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowStateContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PatientStateMapper implements EntityMapper<PatientState, PatientStateModel> {

    @Autowired
    protected LightService<PatientProgramLight, PatientProgramContext> patientProgramService;

    @Autowired
    protected LightService<ProgramWorkflowStateLight, ProgramWorkflowStateContext> programWorkflowStateService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "patientProgram.uuid", target = "patientProgramUuid")
    @Mapping(source = "patientProgram.program.uuid", target = "patientProgramProgramUuid")
    @Mapping(source = "patientProgram.program.concept.uuid", target = "patientProgramProgramConceptUuid")
    @Mapping(source = "patientProgram.program.concept.conceptClass.uuid", target = "patientProgramProgramConceptClassUuid")
    @Mapping(source = "patientProgram.program.concept.datatype.uuid", target = "patientProgramProgramConceptDatatypeUuid")
    @Mapping(source = "patientProgram.patient.uuid", target = "patientProgramPatientUuid")
    @Mapping(source = "state.uuid", target = "stateUuid")
    @Mapping(source = "state.concept.uuid", target = "stateConceptUuid")
    @Mapping(source = "state.concept.conceptClass.uuid", target = "stateConceptClassUuid")
    @Mapping(source = "state.concept.datatype.uuid", target = "stateConceptDatatypeUuid")
    @Mapping(source = "state.programWorkflow.uuid", target = "stateWorkflowUuid")
    @Mapping(source = "state.programWorkflow.concept.uuid", target = "stateWorkflowConceptUuid")
    @Mapping(source = "state.programWorkflow.concept.conceptClass.uuid", target = "stateWorkflowConceptClassUuid")
    @Mapping(source = "state.programWorkflow.concept.datatype.uuid", target = "stateWorkflowConceptDatatypeUuid")
    @Mapping(source = "state.programWorkflow.program.uuid", target = "stateWorkflowProgramUuid")
    @Mapping(source = "state.programWorkflow.program.concept.uuid", target = "stateWorkflowProgramConceptUuid")
    @Mapping(source = "state.programWorkflow.program.concept.conceptClass.uuid", target = "stateWorkflowProgramConceptClassUuid")
    @Mapping(source = "state.programWorkflow.program.concept.datatype.uuid", target = "stateWorkflowProgramConceptDatatypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract PatientStateModel entityToModel(final PatientState entity);

    @Override
    @Mapping(expression = "java(getOrInitPatientProgram(model))", target = "patientProgram")
    @Mapping(expression = "java(getOrInitState(model))", target = "state")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract PatientState modelToEntity(final PatientStateModel model);

    protected PatientProgramLight getOrInitPatientProgram(final PatientStateModel model) {
        PatientProgramContext context = PatientProgramContext.builder()
                .patientUuid(model.getPatientProgramPatientUuid())
                .programUuid(model.getPatientProgramProgramUuid())
                .programConceptUuid(model.getPatientProgramProgramConceptUuid())
                .programConceptClassUuid(model.getPatientProgramProgramConceptClassUuid())
                .programConceptDatatypeUuid(model.getPatientProgramProgramConceptDatatypeUuid())
                .build();

        return patientProgramService.getOrInit(model.getPatientProgramUuid(), context);
    }

    protected ProgramWorkflowStateLight getOrInitState(final PatientStateModel model) {
        ProgramWorkflowStateContext context = ProgramWorkflowStateContext.builder()
                .conceptUuid(model.getStateConceptUuid())
                .conceptClassUuid(model.getStateConceptClassUuid())
                .conceptDatatypeUuid(model.getStateConceptDatatypeUuid())
                .workflowUuid(model.getStateWorkflowUuid())
                .workflowConceptUuid(model.getStateWorkflowConceptUuid())
                .workflowConceptClassUuid(model.getStateWorkflowConceptClassUuid())
                .workflowConceptDatatypeUuid(model.getStateWorkflowConceptDatatypeUuid())
                .workflowProgramUuid(model.getStateWorkflowProgramUuid())
                .workflowProgramConceptUuid(model.getStateWorkflowProgramConceptUuid())
                .workflowProgramConceptClassUuid(model.getStateWorkflowProgramConceptClassUuid())
                .workflowProgramConceptDatatypeUuid(model.getStateWorkflowProgramConceptDatatypeUuid())
                .build();

        return programWorkflowStateService.getOrInit(model.getStateUuid(), context);
    }
}
