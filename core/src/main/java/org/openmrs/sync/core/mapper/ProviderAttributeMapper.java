package org.openmrs.sync.core.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.ProviderAttribute;
import org.openmrs.sync.core.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = AttributeMapper.class)
public abstract class ProviderAttributeMapper implements EntityMapper<ProviderAttribute, AttributeModel> {

    @Autowired
    protected LightServiceNoContext<ProviderLight> providerService;

    @Autowired
    protected LightServiceNoContext<ProviderAttributeTypeLight> providerAttributeTypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @InheritConfiguration(name = "entityToModel")
    public abstract AttributeModel entityToModel(final ProviderAttribute entity);

    @Override
    @InheritConfiguration(name = "modelToEntity")
    @Mapping(expression = "java(providerAttributeTypeService.getOrInit(model.getAttributeTypeUuid()))", target = "attributeType")
    @Mapping(expression = "java(providerService.getOrInit(model.getReferencedEntityUuid()))", target = "referencedEntity")
    public abstract ProviderAttribute modelToEntity(final AttributeModel model);
}
