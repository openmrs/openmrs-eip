package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.ConceptEty;

public interface ConceptRepository extends OpenMrsRepository<ConceptEty> {

    /**
     * find concept by uuid
     * @param uuid the uuid
     * @return ConceptEty
     */
    ConceptEty findByUuid(final String uuid);
}
