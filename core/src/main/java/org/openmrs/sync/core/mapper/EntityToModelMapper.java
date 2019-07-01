package org.openmrs.sync.core.mapper;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@Component
public class EntityToModelMapper {

    private Function<BaseEntity, Context> instantiateModel;

    private UnaryOperator<Context> copyStandardFields;

    private BiConsumer<Context, PropertyDescriptor> extractUuid;

    private BiFunction<Context, BiConsumer<Context, PropertyDescriptor>, Context> forEachLinkedEntity;

    public EntityToModelMapper(final Function<BaseEntity, Context> instantiateModel,
                               final UnaryOperator<Context> copyStandardFields,
                               final BiConsumer<Context, PropertyDescriptor> extractUuid,
                               final BiFunction<Context, BiConsumer<Context, PropertyDescriptor>, Context> forEachLinkedEntity) {
        this.instantiateModel = instantiateModel;
        this.copyStandardFields = copyStandardFields;
        this.extractUuid = extractUuid;
        this.forEachLinkedEntity = forEachLinkedEntity;
    }

    public BaseModel entityToModel(final BaseEntity entity) {

        return instantiateModel
                .andThen(copyStandardFields)
                .andThen(forEachLinkedEntity(extractUuid))
                .apply(entity)
                .getModel();
    }

    private UnaryOperator<Context> forEachLinkedEntity(final BiConsumer<Context, PropertyDescriptor> extractUuid) {
        return context -> forEachLinkedEntity.apply(context, extractUuid);
    }
}
