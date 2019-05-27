package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.AuditableEntity;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.repository.AuditableRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public abstract class AbstractEntityService<E extends AuditableEntity, M extends OpenMrsModel> implements EntityService<M> {

    private AuditableRepository<E> repository;
    private Function<E, M> entityToModelMapper;
    private Function<M, E> modelToEntityMapper;

    public AbstractEntityService(final AuditableRepository<E> repository,
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
    public abstract TableNameEnum getTableName();

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
    public List<M> getModels(final LocalDateTime lastSyncDate) {
        List<E> entities;
        if (lastSyncDate == null) {
            entities = repository.findAll();
        } else {
            entities = repository.findModelsChangedAfterDate(lastSyncDate);
        }

        return entities.stream()
                .map(entityToModelMapper)
                .collect(Collectors.toList());
    }
}
