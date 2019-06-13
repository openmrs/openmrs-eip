package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.service.light.impl.context.Context;

public interface LightService<E extends LightEntity, C extends Context> {

    /**
     * Gets the entity with the given uuid or creates it
     * @param uuid the uuid
     * @param context the context
     * @return entity
     */
    E getOrInit(String uuid, C context);
}
