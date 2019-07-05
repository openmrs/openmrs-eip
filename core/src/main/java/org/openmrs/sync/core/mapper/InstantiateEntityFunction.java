package org.openmrs.sync.core.mapper;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.MapperService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("instantiateEntity")
public class InstantiateEntityFunction<E extends BaseEntity, M extends BaseModel> implements Function<M, Context> {

    private MapperService<E, M> mapperService;

    public InstantiateEntityFunction(final MapperService<E, M> mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public Context<E, M> apply(final M model) {
        Class<E> modelClass = mapperService.getCorrespondingEntityClass(model);
        try {
            E instanciatedEntity = modelClass.newInstance();
            return new Context<>(instanciatedEntity, model, MappingDirectionEnum.MODEL_TO_ENTITY);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new OpenMrsSyncException("error while instantiating entity " + modelClass, e);
        }
    }
}
