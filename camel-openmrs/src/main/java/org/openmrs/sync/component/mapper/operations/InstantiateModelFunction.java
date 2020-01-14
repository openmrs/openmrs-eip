package org.openmrs.sync.component.mapper.operations;

import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.exception.OpenmrsSyncException;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.MapperService;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
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
            M instanciatedModel = modelClass.getConstructor().newInstance();
            return new Context<>(entity, instanciatedModel, MappingDirectionEnum.ENTITY_TO_MODEL);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new OpenmrsSyncException("cause while instantiating entity " + modelClass, e);
        }
    }
}
