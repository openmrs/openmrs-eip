package org.cicr.sync.core.service.impl;

import org.cicr.sync.core.entity.ConceptEty;
import org.cicr.sync.core.repository.ConceptRepository;
import org.springframework.stereotype.Service;

@Service
public class ConceptService {

    private ConceptRepository conceptRepository;

    public ConceptService(final ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    public ConceptEty getOrInitConcept(final String uuid) {
        ConceptEty concept = conceptRepository.findByUuid(uuid);

        if (concept == null) {
            concept = new ConceptEty();
            concept.setUuid(uuid);
            concept.setClassId(1);
            concept.setDatatypeId(1);
            concept.setCreator(1);
            concept.setDateCreated("1970-01-01 00:00:00");
        }

        return concept;
    }
}
