package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.Concept;

public interface ConceptRepository extends OpenMrsRepository<Concept> {

    /**
     * find concept by uuid
     * @param uuid the uuid
     * @return Concept
     */
    Concept findByUuid(final String uuid);
}
