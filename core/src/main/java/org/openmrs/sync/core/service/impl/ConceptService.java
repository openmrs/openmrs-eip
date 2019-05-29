package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class ConceptService extends AbstractSimpleService<ConceptLight> {

    public ConceptService(final OpenMrsRepository<ConceptLight> conceptRepository) {
        super(conceptRepository);
    }

    @Override
    protected ConceptLight getFakeEntity(final String uuid) {
        ConceptLight concept = new ConceptLight();
        concept.setUuid(uuid);
        concept.setClassId(1);
        concept.setDatatypeId(1);
        concept.setCreator(1L);
        concept.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return concept;
    }
}
