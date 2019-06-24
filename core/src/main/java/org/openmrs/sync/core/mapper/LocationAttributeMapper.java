package org.openmrs.sync.core.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.LocationAttribute;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.service.light.impl.LocationAttributeTypeLightService;
import org.openmrs.sync.core.service.light.impl.LocationLightService;
import org.openmrs.sync.core.service.light.impl.UserLightService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = AttributeMapper.class)
public abstract class LocationAttributeMapper implements EntityMapper<LocationAttribute, AttributeModel> {

    @Autowired
    protected LocationLightService locationService;

    @Autowired
    protected LocationAttributeTypeLightService locationAttributeTypeService;

    @Autowired
    protected UserLightService userService;

    @Override
    @InheritConfiguration(name = "entityToModel")
    public abstract AttributeModel entityToModel(final LocationAttribute entity);

    @Override
    @InheritConfiguration(name = "modelToEntity")
    @Mapping(expression = "java(locationAttributeTypeService.getOrInit(model.getAttributeTypeUuid()))", target = "attributeType")
    @Mapping(expression = "java(locationService.getOrInit(model.getReferencedEntityUuid()))", target = "referencedEntity")
    public abstract LocationAttribute modelToEntity(final AttributeModel model);
}
