package org.cicr.sync.core.service;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.mapper.entityToModel.EntityToModelMapper;
import org.cicr.sync.core.mapper.modelToEntity.ModelToEntityMapper;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.repository.OpenMrsRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractEntityService<E extends OpenMrsEty, M extends OpenMrsModel> implements EntityService<E, M> {

    public abstract EntityNameEnum getEntityName();

    protected abstract OpenMrsRepository<E> getRepository();

    protected abstract EntityToModelMapper<E, M> getEntityToModelMapper();

    protected abstract ModelToEntityMapper<M, E> getModelToEntityMapper();

    @Override
    public E save(M model) {
        E etyInDb = getRepository().findByUuid(model.getUuid());

        E ety = getModelToEntityMapper().apply(model);
        if (etyInDb != null) {
            ety.setId(etyInDb.getId());
        }

        return getRepository().save(ety);
    }

    @Override
    public List<M> getModels() {
        List<E> entities = getRepository().findAll();

        return entities.stream()
                .map(getEntityToModelMapper())
                .collect(Collectors.toList());
    }
}
