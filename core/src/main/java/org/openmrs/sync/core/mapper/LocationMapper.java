package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Location;
import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.LocationModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class LocationMapper implements EntityMapper<Location, LocationModel> {

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "parentLocation.uuid", target = "parentLocationUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "retiredBy.uuid", target = "retiredByUuid")
    public abstract LocationModel entityToModel(final Location entity);

    @Override
    @Mapping(expression = "java(locationService.getOrInit(model.getParentLocationUuid()))", target = "parentLocation")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getRetiredByUuid()))", target = "retiredBy")
    @Mapping(ignore = true, target = "id")
    public abstract Location modelToEntity(final LocationModel model);
}
