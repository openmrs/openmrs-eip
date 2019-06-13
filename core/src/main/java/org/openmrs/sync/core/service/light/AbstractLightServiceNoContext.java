package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.impl.context.EmptyContext;

public abstract class AbstractLightServiceNoContext<E extends LightEntity>
        extends AbstractLightService<E, EmptyContext>
        implements LightServiceNoContext<E> {

    public AbstractLightServiceNoContext(final OpenMrsRepository<E> repository) {
        super(repository);
    }

    /**
     * Creates an entity with only mandatory attributes
     * @param uuid the uuid
     * @return entity
     */
    protected abstract E getShadowEntity(final String uuid);

    @Override
    protected E getShadowEntity(final String uuid,
                                final EmptyContext context) {
        return getShadowEntity(uuid);
    }

    @Override
    public E getOrInit(final String uuid) {
        return super.getOrInit(uuid, new EmptyContext());
    }
}
