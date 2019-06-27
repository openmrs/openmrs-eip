package org.openmrs.sync.core.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.VisitAttribute;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.model.VisitAttributeModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = AttributeMapper.class)
public abstract class VisitAttributeMapper implements EntityMapper<VisitAttribute, VisitAttributeModel> {

    @Autowired
    protected LightService<VisitLight, VisitContext> visitService;

    @Autowired
    protected LightServiceNoContext<VisitAttributeTypeLight> visitAttributeTypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @InheritConfiguration(name = "entityToModel")
    @Mapping(source = "referencedEntity.patient.uuid", target = "patientUuid")
    @Mapping(source = "referencedEntity.visitType.uuid", target = "visitTypeUuid")
    public abstract VisitAttributeModel entityToModel(final VisitAttribute entity);

    @Override
    @InheritConfiguration(name = "modelToEntity")
    @Mapping(expression = "java(visitAttributeTypeService.getOrInit(model.getAttributeTypeUuid()))", target = "attributeType")
    @Mapping(expression = "java(getOrInitVisit(model))", target = "referencedEntity")
    public abstract VisitAttribute modelToEntity(final VisitAttributeModel model);

    protected VisitLight getOrInitVisit(final VisitAttributeModel model) {
        VisitContext context = VisitContext.builder()
                .patientUuid(model.getPatientUuid())
                .visitTypeUuid(model.getVisitTypeUuid())
                .build();

        return visitService.getOrInit(model.getReferencedEntityUuid(), context);
    }
}
