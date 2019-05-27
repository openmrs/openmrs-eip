package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class ConceptService extends AbstractSimpleService<Concept> {

    public ConceptService(final OpenMrsRepository<Concept> conceptRepository) {
        super(conceptRepository);
    }

    @Override
    protected Concept getFakeEntity(final String uuid) {
        Concept concept = new Concept();
        concept.setUuid(uuid);
        concept.setClassId(1);
        concept.setDatatypeId(1);
        concept.setCreator(1);
        concept.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return concept;
    }
}
