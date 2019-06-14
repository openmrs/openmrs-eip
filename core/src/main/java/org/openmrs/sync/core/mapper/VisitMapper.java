package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class VisitMapper implements EntityMapper<Visit, VisitModel> {

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Autowired
    protected LightServiceNoContext<VisitTypeLight> visitTypeService;

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Autowired
    protected LightServiceNoContext<PatientLight> patientService;

    @Override
    @Mapping(source = "visitType.uuid", target = "visitTypeUuid")
    @Mapping(source = "location.uuid", target = "locationUuid")
    @Mapping(source = "patient.uuid", target = "patientUuid")
    @Mapping(source = "indicationConcept.uuid", target = "indicationConceptUuid")
    @Mapping(source = "indicationConcept.conceptClass.uuid", target = "indicationConceptClassUuid")
    @Mapping(source = "indicationConcept.datatype.uuid", target = "indicationConceptDatatypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract VisitModel entityToModel(final Visit entity);

    @Override
    @Mapping(expression = "java(visitTypeService.getOrInit(model.getVisitTypeUuid()))", target ="visitType")
    @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target ="location")
    @Mapping(expression = "java(patientService.getOrInit(model.getPatientUuid()))", target ="patient")
    @Mapping(expression = "java(getOrInitIndicationConcept(model))", target ="indicationConcept")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract Visit modelToEntity(final VisitModel model);

    protected ConceptLight getOrInitIndicationConcept(final VisitModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getIndicationConceptClassUuid())
                .conceptDatatypeUuid(model.getIndicationConceptDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getIndicationConceptUuid(), context);
    }
}
