package org.openmrs.sync.core.service;

import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.core.entity.OpenMrsEty;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public abstract class AbstractEntityService<E extends OpenMrsEty, M extends OpenMrsModel> implements EntityService<M> {

    private OpenMrsRepository<E> repository;
    private Function<E, M> entityToModelMapper;
    private Function<M, E> modelToEntityMapper;

    public AbstractEntityService(final OpenMrsRepository<E> repository,
                                 final Function<E, M> entityToModelMapper,
                                 final Function<M, E> modelToEntityMapper) {
        assertNotNull(repository);
        assertNotNull(entityToModelMapper);
        assertNotNull(modelToEntityMapper);
        this.repository = repository;
        this.entityToModelMapper = entityToModelMapper;
        this.modelToEntityMapper = modelToEntityMapper;
    }

    /**
     * get the service entity name
     * @return enum
     */
    public abstract TableNameEnum getEntityName();

    @Override
    public M save(M model) {
        E etyInDb = repository.findByUuid(model.getUuid());

        E ety = modelToEntityMapper.apply(model);
        if (etyInDb != null) {
            ety.setId(etyInDb.getId());
        }

        return entityToModelMapper.apply(repository.save(ety));
    }

    @Override
    public List<M> getModels() {
        List<E> entities = repository.findAll();

        return entities.stream()
                .map(entityToModelMapper)
                .collect(Collectors.toList());
    }
}
