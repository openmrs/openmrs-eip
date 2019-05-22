package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.OpenMrsEty;
import org.openmrs.sync.core.repository.OpenMrsRepository;

public abstract class AbstractSimpleService<E extends OpenMrsEty> implements SimpleService<E> {

    protected abstract E getFakeEntity(String uuid);

    protected abstract OpenMrsRepository<E> getRepository();

    @Override
    public E getOrInit(String uuid) {
        E user = getRepository().findByUuid(uuid);

        if (user == null) {
            user = getFakeEntity(uuid);
        }

        return getRepository().save(user);
    }
}
