package org.openmrs.sync.core.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.function.UnaryOperator;

@Component
public class CopyStandardFieldsFunction implements UnaryOperator<Context> {

    @Override
    public Context apply(final Context context) {
        BaseModel model = context.getModel();
        BaseEntity entity = context.getEntity();

        copyFieldsFromEntityToModel(model, entity);

        return context;
    }

    private void copyFieldsFromEntityToModel(final BaseModel model,
                                             final BaseEntity entity) {
        try {
            PropertyUtils.copyProperties(model, entity);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new OpenMrsSyncException("error while copying entity " + entity.getClass() + " to model " + model.getClass(), e);
        }
    }
}
