package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.PersonAttribute;
import org.openmrs.sync.core.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PersonAttributeModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PersonAttributeMapper implements EntityMapper<PersonAttribute, PersonAttributeModel> {

    @Autowired
    protected LightServiceNoContext<PersonLight> personService;

    @Autowired
    protected LightServiceNoContext<PersonAttributeTypeLight> personAttributeTypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mappings({
            @Mapping(source = "person.uuid", target = "personUuid"),
            @Mapping(source = "personAttributeType.uuid", target = "personAttributeTypeUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    public abstract PersonAttributeModel entityToModel(final PersonAttribute entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(personService.getOrInit(model.getPersonUuid()))", target = "person"),
            @Mapping(expression = "java(personAttributeTypeService.getOrInit(model.getPersonAttributeTypeUuid()))", target = "personAttributeType"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target = "voidedBy")
    })
    public abstract PersonAttribute modelToEntity(final PersonAttributeModel model);
}
