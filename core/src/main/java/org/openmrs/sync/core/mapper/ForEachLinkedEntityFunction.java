package org.openmrs.sync.core.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.sync.core.entity.BaseEntity;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Component
public class ForEachLinkedEntityFunction implements BiFunction<Context, BiConsumer<Context, PropertyDescriptor>, Context> {

    @Override
    public Context apply(final Context context,
                         final BiConsumer<Context, PropertyDescriptor> action) {
        PropertyDescriptor[] descs = PropertyUtils.getPropertyDescriptors(context.getEntity());
        Stream.of(descs)
                .filter(desc -> BaseEntity.class.isAssignableFrom(desc.getReadMethod().getReturnType()))
                .forEach(desc -> action.accept(context, desc));

        return context;
    }
}
