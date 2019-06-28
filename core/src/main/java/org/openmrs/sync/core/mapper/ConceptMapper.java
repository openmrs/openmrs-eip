package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.ConceptModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ConceptMapper implements EntityMapper<Concept, ConceptModel> {

    @Autowired
    protected LightServiceNoContext<ConceptClassLight> conceptClassService;

    @Autowired
    protected LightServiceNoContext<ConceptDatatypeLight> datatypeService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "conceptClass.uuid", target = "conceptClassUuid")
    @Mapping(source = "datatype.uuid", target = "datatypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "retiredBy.uuid", target = "retiredByUuid")
    public abstract ConceptModel entityToModel(final Concept entity);

    @Override
    @Mapping(expression = "java(conceptClassService.getOrInit(model.getConceptClassUuid()))", target = "conceptClass")
    @Mapping(expression = "java(datatypeService.getOrInit(model.getDatatypeUuid()))", target = "datatype")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getRetiredByUuid()))", target = "retiredBy")
    @Mapping(ignore = true, target = "id")
    public abstract Concept modelToEntity(final ConceptModel model);
}
