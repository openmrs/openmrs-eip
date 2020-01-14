package org.openmrs.sync.component.mapper.operations;

import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.exception.OpenmrsSyncException;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.MapperService;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

@Component("instantiateEntity")
public class InstantiateEntityFunction<E extends BaseEntity, M extends BaseModel> implements Function<M, Context> {

    private MapperService<E, M> mapperService;

    public InstantiateEntityFunction(final MapperService<E, M> mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public Context<E, M> apply(final M model) {
        Class<E> entityClass = mapperService.getCorrespondingEntityClass(model);
        try {
            E instanciatedEntity = entityClass.getDeclaredConstructor().newInstance();
            return new Context<>(instanciatedEntity, model, MappingDirectionEnum.MODEL_TO_ENTITY);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new OpenmrsSyncException("cause while instantiating entity " + entityClass, e);
        }
    }
}
