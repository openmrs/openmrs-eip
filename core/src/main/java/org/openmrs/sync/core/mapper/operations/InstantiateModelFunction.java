package org.openmrs.sync.core.mapper.operations;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.MapperService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("instantiateModel")
public class InstantiateModelFunction<E extends BaseEntity, M extends BaseModel> implements Function<E, Context> {

    private MapperService<E, M> mapperService;

    public InstantiateModelFunction(final MapperService<E, M> mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public Context<E, M> apply(final E entity) {
        Class<M> modelClass = mapperService.getCorrespondingModelClass(entity);
        try {
            M instanciatedModel = modelClass.newInstance();
            return new Context<>(entity, instanciatedModel, MappingDirectionEnum.ENTITY_TO_MODEL);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new OpenMrsSyncException("error while instantiating entity " + modelClass, e);
        }
    }
}
