package org.openmrs.sync.core.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.LocationAttribute;
import org.openmrs.sync.core.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = AttributeMapper.class)
public abstract class LocationAttributeMapper implements EntityMapper<LocationAttribute, AttributeModel> {

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightServiceNoContext<LocationAttributeTypeLight> locationAttributeTypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @InheritConfiguration(name = "entityToModel")
    public abstract AttributeModel entityToModel(final LocationAttribute entity);

    @Override
    @InheritConfiguration(name = "modelToEntity")
    @Mapping(expression = "java(locationAttributeTypeService.getOrInit(model.getAttributeTypeUuid()))", target = "attributeType")
    @Mapping(expression = "java(locationService.getOrInit(model.getReferencedEntityUuid()))", target = "referencedEntity")
    public abstract LocationAttribute modelToEntity(final AttributeModel model);
}
