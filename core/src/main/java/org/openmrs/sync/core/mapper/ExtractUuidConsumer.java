package org.openmrs.sync.core.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

@Component
public class ExtractUuidConsumer implements BiConsumer<Context, PropertyDescriptor> {

    @Override
    public void accept(final Context context, final PropertyDescriptor propertyDescriptor) {
        setAttributeFromEntityToModel(context.getEntity(), context.getModel(), propertyDescriptor);
    }

    private void setAttributeFromEntityToModel(final BaseEntity entity,
                                               final BaseModel newModelInstance,
                                               final PropertyDescriptor pd) {
        Method readMethod = pd.getReadMethod();
        BaseEntity linkedEntity = invokeMethodFromEntity(entity, readMethod);
        if (linkedEntity != null) {
            String uuid = linkedEntity.getUuid();
            String attributeName = pd.getDisplayName();
            setProperty(newModelInstance, uuid, attributeName);
        }
    }

    private BaseEntity invokeMethodFromEntity(final BaseEntity entity,
                                              final Method readMethod) {
        try {
            return (BaseEntity) readMethod.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new OpenMrsSyncException("error while invoking method " + readMethod + " in entity " + entity.getClass(), e);
        }
    }

    private <M extends BaseModel> void setProperty(final M newModelInstance,
                                                   final String uuid,
                                                   final String attributeName) {
        try {
            PropertyUtils.setProperty(newModelInstance, attributeName + "Uuid", uuid);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new OpenMrsSyncException("error setting property " + attributeName + "Uuid to model " + newModelInstance.getClass(), e);
        }
    }
}
