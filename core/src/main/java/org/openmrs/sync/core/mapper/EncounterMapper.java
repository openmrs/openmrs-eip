package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.impl.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public abstract class EncounterMapper implements EntityMapper<Encounter, EncounterModel> {

    @Autowired
    protected EncounterTypeLightService encounterTypeService;

    @Autowired
    protected PatientLightService patientService;

    @Autowired
    protected LocationLightService locationService;

    @Autowired
    protected FormLightService formService;

    @Autowired
    protected VisitLightService visitService;

    @Autowired
    protected UserLightService userService;

    @Override
    @Mappings({
            @Mapping(source = "encounterType.uuid", target = "encounterTypeUuid"),
            @Mapping(source = "patient.uuid", target = "patientUuid"),
            @Mapping(source = "location.uuid", target = "locationUuid"),
            @Mapping(source = "form.uuid", target = "formUuid"),
            @Mapping(source = "visit.uuid", target = "visitUuid"),
            @Mapping(source = "visit.patient.uuid", target = "visitPatientUuid"),
            @Mapping(source = "visit.visitType.uuid", target = "visitVisitTypeUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    public abstract EncounterModel entityToModel(final Encounter entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(encounterTypeService.getOrInit(model.getEncounterTypeUuid()))", target = "encounterType"),
            @Mapping(expression = "java(patientService.getOrInit(model.getPatientUuid()))", target = "patient"),
            @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target = "location"),
            @Mapping(expression = "java(formService.getOrInit(model.getFormUuid()))", target = "form"),
            @Mapping(expression = "java(getOrInitVisit(model))", target = "visit"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target = "voidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Encounter modelToEntity(final EncounterModel model);

    protected VisitLight getOrInitVisit(final EncounterModel model) {
        AttributeUuid patientAttributeUuid = AttributeHelper.buildPatientAttributeUuid(model.getVisitPatientUuid());
        AttributeUuid visitTypeAttributeUuid = AttributeHelper.buildVisitTypeAttributeUuid(model.getVisitVisitTypeUuid());
        return visitService.getOrInit(model.getVisitUuid(), Arrays.asList(patientAttributeUuid, visitTypeAttributeUuid));
    }
}
