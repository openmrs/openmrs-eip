package org.openmrs.eip.component.mapper;

import org.openmrs.eip.component.mapper.operations.Context;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Component
public class ModelToEntityMapper<M extends BaseModel, E extends BaseEntity> implements Function<M, E> {
	
	private Function<M, Context<E, M>> instantiateEntity;
	
	private UnaryOperator<Context<E, M>> copyStandardFields;
	
	private BiConsumer<Context<E, M>, String> linkLightEntity;
	
	private BiFunction<Context<E, M>, BiConsumer<Context<E, M>, String>, E> forEachUuidAttribute;
	
	public ModelToEntityMapper(final Function<M, Context<E, M>> instantiateEntity,
	    final UnaryOperator<Context<E, M>> copyStandardFields, final BiConsumer<Context<E, M>, String> linkLightEntity,
	    final BiFunction<Context<E, M>, BiConsumer<Context<E, M>, String>, E> forEachUuidAttribute) {
		this.instantiateEntity = instantiateEntity;
		this.copyStandardFields = copyStandardFields;
		this.linkLightEntity = linkLightEntity;
		this.forEachUuidAttribute = forEachUuidAttribute;
	}
	
	@Override
	public E apply(final M model) {
		return instantiateEntity.andThen(copyStandardFields).andThen(forEachUuidAttribute(linkLightEntity)).apply(model);
	}
	
	private Function<Context<E, M>, E> forEachUuidAttribute(final BiConsumer<Context<E, M>, String> linkEntity) {
		return context -> forEachUuidAttribute.apply(context, linkEntity);
	}
}
