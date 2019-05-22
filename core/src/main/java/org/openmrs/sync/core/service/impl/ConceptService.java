package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.ConceptEty;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class ConceptService extends AbstractSimpleService<ConceptEty> {

    private OpenMrsRepository<ConceptEty> conceptRepository;

    public ConceptService(final OpenMrsRepository<ConceptEty> conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    @Override
    protected OpenMrsRepository<ConceptEty> getRepository() {
        return conceptRepository;
    }

    @Override
    protected ConceptEty getFakeEntity(final String uuid) {
        ConceptEty concept = new ConceptEty();
        concept.setUuid(uuid);
        concept.setClassId(1);
        concept.setDatatypeId(1);
        concept.setCreator(1);
        concept.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return concept;
    }
}
