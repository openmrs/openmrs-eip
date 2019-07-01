package org.openmrs.sync.core.mapper;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.MapperService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class InstantiateModelFunction implements Function<BaseEntity, Context> {

    private MapperService mapperService;

    public InstantiateModelFunction(final MapperService mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public Context apply(final BaseEntity entity) {
        Class<? extends BaseModel> modelClass = mapperService.getCorrespondingModelClass(entity);
        try {
            BaseModel instanciatedModel = modelClass.newInstance();
            return Context.builder()
                    .entity(entity)
                    .model(instanciatedModel)
                    .build();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new OpenMrsSyncException("error while instantiating entity " + modelClass, e);
        }
    }
}
