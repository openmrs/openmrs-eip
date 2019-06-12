package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public abstract class AbstractLightService<E extends LightEntity> implements LightService<E> {

    protected static final String DEFAULT_STRING= "[Default]";
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
    protected abstract E getFakeEntity(String uuid, List<AttributeUuid> uuids);

    @Override
    public E getOrInit(final String uuid,
                       final List<AttributeUuid> attributeUuids) {
        if (uuid == null) {
            return null;
        }

        E entity = repository.findByUuid(uuid);

        if (entity == null) {
            entity = getFakeEntity(uuid, attributeUuids);

            entity = repository.save(entity);
        }

        return entity;
    }
}
