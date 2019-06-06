package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptLightService extends AbstractLightService<ConceptLight> {

    public ConceptLightService(final OpenMrsRepository<ConceptLight> conceptRepository) {
        super(conceptRepository);
    }

    @Override
    protected ConceptLight getFakeEntity(final String uuid) {
        ConceptLight concept = new ConceptLight();
        concept.setUuid(uuid);
        concept.setClassId(1L);
        concept.setDatatypeId(1L);
        concept.setCreator(1L);
        concept.setDateCreated(DEFAULT_DATE);
        return concept;
    }
}
