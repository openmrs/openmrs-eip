package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.service.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class VisitMapper implements EntityMapper<Visit, VisitModel> {

    @Autowired
    protected SimpleService<UserLight> userService;

    @Autowired
    protected SimpleService<VisitTypeLight> visitTypeService;

    @Autowired
    protected SimpleService<LocationLight> locationService;

    @Autowired
    protected SimpleService<ConceptLight> conceptService;

    @Autowired
    protected SimpleService<PatientLight> patientService;

    @Override
    @Mappings({
            @Mapping(source = "visitType.uuid", target = "visitTypeUuid"),
            @Mapping(source = "location.uuid", target = "locationUuid"),
            @Mapping(source = "patient.uuid", target = "patientUuid"),
            @Mapping(source = "indicationConcept.uuid", target = "indicationConceptUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    public abstract VisitModel entityToModel(final Visit entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(visitTypeService.getOrInit(model.getVisitTypeUuid()))", target ="visitType"),
            @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target ="location"),
            @Mapping(expression = "java(patientService.getOrInit(model.getPatientUuid()))", target ="patient"),
            @Mapping(expression = "java(conceptService.getOrInit(model.getIndicationConceptUuid()))", target ="indicationConcept"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Visit modelToEntity(final VisitModel model);
}
