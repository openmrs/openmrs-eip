package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.light.ConceptLight;

public interface ConceptRepository extends OpenMrsRepository<ConceptLight> {

    /**
     * find concept by uuid
     * @param uuid the uuid
     * @return ConceptLight
     */
    ConceptLight findByUuid(final String uuid);
}
