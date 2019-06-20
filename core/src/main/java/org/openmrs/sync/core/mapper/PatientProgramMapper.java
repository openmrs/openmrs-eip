package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.PatientProgram;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.PatientProgramModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PatientProgramMapper implements EntityMapper<PatientProgram, PatientProgramModel> {

    @Autowired
    protected LightServiceNoContext<PatientLight> patientService;

    @Autowired
    protected LightService<ProgramLight, ProgramContext> programService;

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "patient.uuid", target = "patientUuid")
    @Mapping(source = "program.uuid", target = "programUuid")
    @Mapping(source = "program.concept.uuid", target = "programConceptUuid")
    @Mapping(source = "program.concept.conceptClass.uuid", target = "programConceptClassUuid")
    @Mapping(source = "program.concept.datatype.uuid", target = "programConceptDatatypeUuid")
    @Mapping(source = "location.uuid", target = "locationUuid")
    @Mapping(source = "outcomeConcept.uuid", target = "outcomeConceptUuid")
    @Mapping(source = "outcomeConcept.conceptClass.uuid", target = "outcomeConceptClassUuid")
    @Mapping(source = "outcomeConcept.datatype.uuid", target = "outcomeConceptDatatypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract PatientProgramModel entityToModel(final PatientProgram entity);

    @Override
    @Mapping(expression = "java(patientService.getOrInit(model.getPatientUuid()))", target ="patient")
    @Mapping(expression = "java(getOrInitProgram(model))", target ="program")
    @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target ="location")
    @Mapping(expression = "java(getOrInitConcept(model))", target ="outcomeConcept")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract PatientProgram modelToEntity(final PatientProgramModel model);

    protected ConceptLight getOrInitConcept(final PatientProgramModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getOutcomeConceptClassUuid())
                .conceptDatatypeUuid(model.getOutcomeConceptDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getOutcomeConceptUuid(), context);
    }

    protected ProgramLight getOrInitProgram(final PatientProgramModel model) {
        ProgramContext context = ProgramContext.builder()
                .conceptUuid(model.getProgramConceptUuid())
                .conceptClassUuid(model.getProgramConceptClassUuid())
                .conceptDatatypeUuid(model.getProgramConceptDatatypeUuid())
                .build();

        return programService.getOrInit(model.getProgramUuid(), context);
    }
}
