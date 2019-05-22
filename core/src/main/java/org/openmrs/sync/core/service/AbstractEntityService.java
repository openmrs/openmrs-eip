package org.openmrs.sync.core.service;

import org.openmrs.sync.core.camel.EntityNameEnum;
import org.openmrs.sync.core.entity.OpenMrsEty;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractEntityService<E extends OpenMrsEty, M extends OpenMrsModel> implements EntityService<M> {

    public abstract EntityNameEnum getEntityName();

    protected abstract OpenMrsRepository<E> getRepository();

    protected abstract Function<E, M> getEntityToModelMapper();

    protected abstract Function<M, E> getModelToEntityMapper();

    @Override
    public M save(M model) {
        E etyInDb = getRepository().findByUuid(model.getUuid());

        E ety = getModelToEntityMapper().apply(model);
        if (etyInDb != null) {
            ety.setId(etyInDb.getId());
        }

        return getEntityToModelMapper().apply(getRepository().save(ety));
    }

    @Override
    public List<M> getModels() {
        List<E> entities = getRepository().findAll();

        return entities.stream()
                .map(getEntityToModelMapper())
                .collect(Collectors.toList());
    }
}
