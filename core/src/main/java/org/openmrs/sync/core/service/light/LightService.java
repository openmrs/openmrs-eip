package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.BaseEntity;

public interface LightService<E extends BaseEntity> {

    /**
     * Gets the entity with the given uuid or creates it
     * @param uuid the uuid
     * @return entity
     */
    E getOrInit(final String uuid);
}
