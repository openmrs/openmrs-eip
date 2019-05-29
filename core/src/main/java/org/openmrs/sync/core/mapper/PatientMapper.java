package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.service.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PatientMapper implements EntityMapper<Patient, PatientModel> {

    @Autowired
    protected SimpleService<ConceptLight> conceptService;

    @Autowired
    protected SimpleService<UserLight> userService;

    @Override
    @Mappings({
            @Mapping(source = "causeOfDeath.uuid", target = "causeOfDeathUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid"),
            @Mapping(source = "creator.uuid", target = "personCreatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "personChangedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "personVoidedByUuid")
    })
    public abstract PatientModel entityToModel(final Patient entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(conceptService.getOrInit(model.getCauseOfDeathUuid()))", target ="causeOfDeath"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonCreatorUuid()))", target ="personCreator"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonChangedByUuid()))", target ="personChangedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonVoidedByUuid()))", target ="personVoidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Patient modelToEntity(final PatientModel model);
}
