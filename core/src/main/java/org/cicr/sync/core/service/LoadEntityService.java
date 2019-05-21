package org.cicr.sync.core.service;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.mapper.entitiesToModel.EntityToModelMapper;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.repository.OpenMrsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class LoadEntityService<E extends OpenMrsEty, M extends OpenMrsModel> {

    public abstract EntityNameEnum getEntityName();

    protected abstract OpenMrsRepository<E> getRepository();

    protected abstract EntityToModelMapper<E, M> getEntityToModelMapper();

    public List<M> getModels() {
        List<E> entities = getRepository().findAll();

        return entities.stream()
                .map(entity -> getEntityToModelMapper().apply(entity))
                .collect(Collectors.toList());
    }
}
