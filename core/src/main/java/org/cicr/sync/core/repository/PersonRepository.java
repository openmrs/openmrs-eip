package org.cicr.sync.core.repository;

import org.cicr.sync.core.entity.PersonEty;

public interface PersonRepository extends OpenMrsRepository<PersonEty> {

    @Override
    PersonEty findByUuid(final String uuid);
}
