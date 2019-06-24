package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.AttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

public abstract class AbstractAttributeTypeLightService<E extends AttributeTypeLight> extends AbstractLightServiceNoContext<E> {

    public AbstractAttributeTypeLightService(final OpenMrsRepository<E> repository) {
        super(repository);
    }

    protected abstract E createEntity();

    @Override
    protected E getShadowEntity(final String uuid) {
        E attributeEntity = createEntity();
        attributeEntity.setUuid(uuid);
        attributeEntity.setDateCreated(DEFAULT_DATE);
        attributeEntity.setCreator(DEFAULT_USER_ID);
        attributeEntity.setName(DEFAULT_STRING);
        return attributeEntity;
    }
}
