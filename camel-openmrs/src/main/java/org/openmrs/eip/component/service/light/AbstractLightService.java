package org.openmrs.eip.component.service.light;

import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.utils.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.Month;

public abstract class AbstractLightService<E extends LightEntity> implements LightService<E> {

    private static final String DEFAULT_UUID_PREFIX= "PLACEHOLDER_";
    public static final String DEFAULT_STRING= "[Default]";
    public static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0);
    public static final long DEFAULT_USER_ID = 1L;

    protected OpenmrsRepository<E> repository;

    public AbstractLightService(final OpenmrsRepository<E> repository) {
        this.repository = repository;
    }

    /**
     * Creates a placeholder entity with only mandatory attributes which will be
     * unique for all entities with the same type.
     * After a round of synchronization no placeholder entity should be left in the db
     * @return the entity
     */
    protected abstract E createPlaceholderEntity(String uuid);

    /**
     * Get the placeholder uuid
     * @return uuid
     */
    private  String getPlaceholderUuid() {
        Class<E> persistentClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];

        return DEFAULT_UUID_PREFIX + StringUtils.fromCamelCaseToSnakeCase(persistentClass.getSimpleName());
    }

    @Override
    public E getOrInitEntity(final String uuid) {
        return getOrInit(uuid);
    }

    @Override
    public E getOrInitPlaceholderEntity() {
        return getOrInit(getPlaceholderUuid());
    }

    private E getOrInit(final String uuid) {
        if (uuid == null) {
            return null;
        }

        E entity = repository.findByUuid(uuid);

        if (entity == null) {
            entity = createPlaceholderEntity(uuid);

            entity.setUuid(uuid);
            voidPlaceholder(entity);

            entity = repository.save(entity);
        }

        return entity;
    }

    private void voidPlaceholder(final E entity) {
        entity.setMuted(true);
        entity.setMuteReason("[placeholder]");
        entity.setDateMuted(DEFAULT_DATE);
        entity.setMutedBy(DEFAULT_USER_ID);
    }
}
