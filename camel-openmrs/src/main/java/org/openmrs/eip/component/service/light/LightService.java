package org.openmrs.eip.component.service.light;

import org.openmrs.eip.component.entity.light.LightEntity;

public interface LightService<E extends LightEntity> {

    /**
     * Gets the entity with the given uuid and creates it if needed
     * @param uuid the uuid
     * @return entity
     */
    E getOrInitEntity(String uuid);

    /**
     * Gets the entity placeholder and creates it if needed
     * @return entity
     */
    E getOrInitPlaceholderEntity();
}
