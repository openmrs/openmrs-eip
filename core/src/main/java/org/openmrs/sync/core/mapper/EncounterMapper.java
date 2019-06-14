package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class EncounterMapper implements EntityMapper<Encounter, EncounterModel> {

    @Autowired
    protected LightServiceNoContext<EncounterTypeLight> encounterTypeService;

    @Autowired
    protected LightServiceNoContext<PatientLight> patientService;

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightServiceNoContext<FormLight> formService;

    @Autowired
    protected LightService<VisitLight, VisitContext> visitService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "encounterType.uuid", target = "encounterTypeUuid")
    @Mapping(source = "patient.uuid", target = "patientUuid")
    @Mapping(source = "location.uuid", target = "locationUuid")
    @Mapping(source = "form.uuid", target = "formUuid")
    @Mapping(source = "visit.uuid", target = "visitUuid")
    @Mapping(source = "visit.patient.uuid", target = "visitPatientUuid")
    @Mapping(source = "visit.visitType.uuid", target = "visitVisitTypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract EncounterModel entityToModel(final Encounter entity);

    @Override
    @Mapping(expression = "java(encounterTypeService.getOrInit(model.getEncounterTypeUuid()))", target = "encounterType")
    @Mapping(expression = "java(patientService.getOrInit(model.getPatientUuid()))", target = "patient")
    @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target = "location")
    @Mapping(expression = "java(formService.getOrInit(model.getFormUuid()))", target = "form")
    @Mapping(expression = "java(getOrInitVisit(model))", target = "visit")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target = "voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract Encounter modelToEntity(final EncounterModel model);

    protected VisitLight getOrInitVisit(final EncounterModel model) {
        VisitContext context = VisitContext.builder()
                .patientUuid(model.getVisitPatientUuid())
                .visitTypeUuid(model.getVisitVisitTypeUuid())
                .build();
        return visitService.getOrInit(model.getVisitUuid(), context);
    }
}
