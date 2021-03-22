package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptDatatypeLight;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.entity.light.ConceptClassLight;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptLightService extends AbstractLightService<ConceptLight> {

    private LightService<ConceptClassLight> conceptClassService;

    private LightService<ConceptDatatypeLight> conceptDatatypeService;

    public ConceptLightService(final OpenmrsRepository<ConceptLight> conceptRepository,
                               final LightService<ConceptClassLight> conceptClassService,
                               final LightService<ConceptDatatypeLight> conceptDatatypeService) {
        super(conceptRepository);
        this.conceptClassService = conceptClassService;
        this.conceptDatatypeService = conceptDatatypeService;
    }

    @Override
    protected ConceptLight createPlaceholderEntity(final String uuid) {
        ConceptLight concept = new ConceptLight();
        concept.setConceptClass(conceptClassService.getOrInitPlaceholderEntity());
        concept.setDatatype(conceptDatatypeService.getOrInitPlaceholderEntity());
        concept.setCreator(1L);
        concept.setDateCreated(DEFAULT_DATE);
        return concept;
    }
}
