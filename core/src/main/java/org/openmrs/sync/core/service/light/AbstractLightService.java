package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

public abstract class AbstractLightService<E extends BaseEntity> implements LightService<E> {

    protected static final String DEFAULT_STRING= "Default";
    protected static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0);
    protected static final long DEFAULT_USER_ID = 1L;

    private OpenMrsRepository<E> repository;

    public AbstractLightService(final OpenMrsRepository<E> repository) {
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
        if (uuid == null) {
            return null;
        }

        E entity = repository.findByUuid(uuid);

        if (entity == null) {
            entity = getFakeEntity(uuid);

            entity = repository.save(entity);
        }

        return entity;
    }
}
