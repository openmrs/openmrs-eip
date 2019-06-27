package org.openmrs.sync.core.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.ConceptAttribute;
import org.openmrs.sync.core.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.ConceptAttributeModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = AttributeMapper.class)
public abstract class ConceptAttributeMapper implements EntityMapper<ConceptAttribute, ConceptAttributeModel> {

    @Autowired
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Autowired
    protected LightServiceNoContext<ConceptAttributeTypeLight> conceptAttributeTypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @InheritConfiguration(name = "entityToModel")
    @Mapping(source = "referencedEntity.conceptClass.uuid", target = "conceptClassUuid")
    @Mapping(source = "referencedEntity.datatype.uuid", target = "conceptDatatypeUuid")
    public abstract ConceptAttributeModel entityToModel(final ConceptAttribute entity);

    @Override
    @InheritConfiguration(name = "modelToEntity")
    @Mapping(expression = "java(conceptAttributeTypeService.getOrInit(model.getAttributeTypeUuid()))", target = "attributeType")
    @Mapping(expression = "java(getOrInitConcept(model))", target = "referencedEntity")
    public abstract ConceptAttribute modelToEntity(final ConceptAttributeModel model);

    protected ConceptLight getOrInitConcept(final ConceptAttributeModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getConceptClassUuid())
                .conceptDatatypeUuid(model.getConceptDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getReferencedEntityUuid(), context);
    }
}
