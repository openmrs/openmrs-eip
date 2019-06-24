package org.openmrs.sync.core.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Attribute;
import org.openmrs.sync.core.model.AttributeModel;

@MapperConfig
public interface AttributeMapper<E extends Attribute, M extends AttributeModel> {

    @Mapping(source = "referencedEntity.uuid", target = "referencedEntityUuid")
    @Mapping(source = "attributeType.uuid", target = "attributeTypeUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "changedBy.uuid", target = "changedByUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    M entityToModel(final E entity);

    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target = "creator")
    @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target = "changedBy")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target = "voidedBy")
    @Mapping(ignore = true, target = "id")
    E modelToEntity(final M model);
}
