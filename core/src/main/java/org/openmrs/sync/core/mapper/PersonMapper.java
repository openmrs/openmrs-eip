package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.light.LightService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PersonMapper implements EntityMapper<Person, PersonModel> {

    @Autowired
    protected LightService<ConceptLight> conceptService;

    @Autowired
    protected LightService<UserLight> userService;

    @Override
    @Mappings({
            @Mapping(source = "causeOfDeath.uuid", target = "causeOfDeathUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    public abstract PersonModel entityToModel(final Person entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(conceptService.getOrInit(model.getCauseOfDeathUuid()))", target ="causeOfDeath"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Person modelToEntity(final PersonModel model);
}
