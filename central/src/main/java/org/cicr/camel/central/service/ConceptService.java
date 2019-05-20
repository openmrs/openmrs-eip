package org.cicr.camel.central.service;

import org.cicr.camel.central.entity.ConceptEty;
import org.cicr.camel.central.repository.ConceptRepository;
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
