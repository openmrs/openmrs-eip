package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.PersonEty;

public interface PersonRepository extends OpenMrsRepository<PersonEty> {

    /**
     * find person by uuid
     * @param uuid the uuid
     * @return PersonEty
     */
    @Override
    PersonEty findByUuid(final String uuid);
}
