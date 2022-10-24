package org.openmrs.eip.component.mapper.operations;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.stereotype.Component;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Component("forEachUuidAttribute")
public class ForEachUuidAttributeFunction<E extends BaseEntity, M extends BaseModel> implements BiFunction<Context<E, M>, BiConsumer<Context<E, M>, String>, E> {
	
	private static final String UUID_SUFFIX = "Uuid";
	
	@Override
	public E apply(final Context<E, M> context, final BiConsumer<Context<E, M>, String> action) {
		PropertyDescriptor[] descs = context.getModelBeanWrapper().getPropertyDescriptors();
		Stream.of(descs).filter(desc -> desc.getName().endsWith(UUID_SUFFIX)).map(FeatureDescriptor::getName)
		        .forEach(attributeName -> action.accept(context, attributeName));
		
		return context.getEntity();
	}
}
