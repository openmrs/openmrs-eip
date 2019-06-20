package org.openmrs.sync.core.mapper;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.entity.light.*;

@Slf4j
public abstract class AbstractMapperTest {

    protected UserLight user = initBaseModel(UserLight.class, "user");

    protected <E extends LightEntity> E initBaseModel(final Class<E> type,
                                                   final String uuid) {
        E instance = null;
        try {
            instance = type.newInstance();
            instance.setUuid(uuid);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("error while instantiating entity " + type.getName(), e);
        }
        return instance;
    }
}
