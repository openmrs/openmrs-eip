package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.service.light.impl.context.Context;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

public abstract class AbstractLightService<E extends LightEntity, C extends Context> implements LightService<E, C> {

    protected static final String DEFAULT_STRING= "[Default]";
    protected static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0);
    protected static final long DEFAULT_USER_ID = 1L;

    private OpenMrsRepository<E> repository;

    public AbstractLightService(final OpenMrsRepository<E> repository) {
        this.repository = repository;
    }

    /**
     * Creates an entity with only mandatory attributes
     * @param uuid the uuid
     * @return the entity
     */
    protected abstract E getShadowEntity(String uuid, C context);

    @Override
    public E getOrInit(final String uuid,
                       final C context) {
        if (uuid == null) {
            return null;
        }

        E entity = repository.findByUuid(uuid);

        if (entity == null) {
            entity = getShadowEntity(uuid, context);

            entity = repository.save(entity);
        }

        return entity;
    }
}
