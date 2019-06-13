package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.springframework.stereotype.Service;

@Service
public class ConceptLightService extends AbstractLightService<ConceptLight, ConceptContext> {

    private LightServiceNoContext<ConceptClassLight> conceptClassService;

    private LightServiceNoContext<ConceptDatatypeLight> conceptDatatypeService;

    public ConceptLightService(final OpenMrsRepository<ConceptLight> conceptRepository,
                               final LightServiceNoContext<ConceptClassLight> conceptClassService,
                               final LightServiceNoContext<ConceptDatatypeLight> conceptDatatypeService) {
        super(conceptRepository);
        this.conceptClassService = conceptClassService;
        this.conceptDatatypeService = conceptDatatypeService;
    }

    @Override
    protected ConceptLight getShadowEntity(final String uuid,
                                           final ConceptContext context) {
        ConceptLight concept = new ConceptLight();
        concept.setUuid(uuid);
        concept.setConceptClass(conceptClassService.getOrInit(context.getConceptClassUuid()));
        concept.setDatatype(conceptDatatypeService.getOrInit(context.getConceptDatatypeUuid()));
        concept.setCreator(1L);
        concept.setDateCreated(DEFAULT_DATE);
        return concept;
    }
}
