package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;

public abstract class AbstractSimpleService<E extends BaseEntity> implements SimpleService<E> {

    private OpenMrsRepository<E> repository;

    public AbstractSimpleService(final OpenMrsRepository<E> repository) {
        this.repository = repository;
    }

    /**
     * Creates an entity with default data
     * @param uuid the uuid
     * @return the entity
     */
    protected abstract E getFakeEntity(String uuid);

    @Override
    public E getOrInit(String uuid) {
        E user = repository.findByUuid(uuid);

        if (user == null) {
            user = getFakeEntity(uuid);
        }

        return repository.save(user);
    }
}
