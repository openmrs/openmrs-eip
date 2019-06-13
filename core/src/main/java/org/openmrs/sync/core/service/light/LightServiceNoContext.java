package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;

public interface LightServiceNoContext<E extends LightEntity> {

    /**
     * Gets the entity with the given uuid or creates it
     * @param uuid the uuid
     * @return entity
     */
    E getOrInit(String uuid);
}
