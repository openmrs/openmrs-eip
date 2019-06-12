package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.service.attribute.AttributeUuid;

import java.util.Collections;
import java.util.List;

public interface LightService<E extends LightEntity> {

    /**
     * Gets the entity with the given uuid or creates it
     * @param uuid the uuid
     * @param uuids
     * @return entity
     */
    E getOrInit(String uuid, List<AttributeUuid> uuids);

    default E getOrInit(final String uuid) {
        return getOrInit(uuid, Collections.emptyList());
    }
}
