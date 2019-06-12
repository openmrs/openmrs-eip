package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public abstract class AbstractEntityService<E extends BaseEntity, M extends BaseModel> implements EntityService<M> {

    private SyncEntityRepository<E> repository;
    private EntityMapper<E, M> mapper;

    public AbstractEntityService(final SyncEntityRepository<E> repository,
                                 final EntityMapper<E, M> mapper) {
        assertNotNull(repository);
        assertNotNull(mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * get the service entity name
     * @return enum
     */
    public abstract EntityNameEnum getEntityName();

    @Override
    public M save(M model) {
        E etyInDb = repository.findByUuid(model.getUuid());

        E ety = mapper.modelToEntity(model);
        if (etyInDb != null) {
            ety.setId(etyInDb.getId());
        }

        return mapper.entityToModel(repository.save(ety));
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
                .map(mapper::entityToModel)
                .collect(Collectors.toList());
    }
}
