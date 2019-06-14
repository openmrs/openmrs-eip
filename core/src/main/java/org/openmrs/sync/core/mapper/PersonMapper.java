package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PersonMapper implements EntityMapper<Person, PersonModel> {

    @Autowired
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "causeOfDeath.uuid", target = "causeOfDeathUuid")
    @Mapping(source = "causeOfDeath.conceptClass.uuid", target = "causeOfDeathClassUuid")
    @Mapping(source = "causeOfDeath.datatype.uuid", target = "causeOfDeathDatatypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract PersonModel entityToModel(final Person entity);

    @Override
    @Mapping(expression = "java(getOrInitCauseOfDeath(model))", target ="causeOfDeath")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract Person modelToEntity(final PersonModel model);

    protected ConceptLight getOrInitCauseOfDeath(final PersonModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getCauseOfDeathClassUuid())
                .conceptDatatypeUuid(model.getCauseOfDeathDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getCauseOfDeathUuid(), context);
    }
}
