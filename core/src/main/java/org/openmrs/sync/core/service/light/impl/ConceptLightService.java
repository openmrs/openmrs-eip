package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptLightService extends AbstractLightService<ConceptLight> {

    private ConceptClassLightService conceptClassService;

    private ConceptDatatypeLightService conceptDatatypeService;

    public ConceptLightService(final OpenMrsRepository<ConceptLight> conceptRepository,
                               final ConceptClassLightService conceptClassService,
                               final ConceptDatatypeLightService conceptDatatypeService) {
        super(conceptRepository);
        this.conceptClassService = conceptClassService;
        this.conceptDatatypeService = conceptDatatypeService;
    }

    @Override
    protected ConceptLight getFakeEntity(final String uuid,
                                         final List<AttributeUuid> attributeUuids) {
        ConceptLight concept = new ConceptLight();
        concept.setUuid(uuid);
        concept.setConceptClass(conceptClassService.getOrInit(AttributeHelper.getConceptClassUuid(attributeUuids)));
        concept.setDatatype(conceptDatatypeService.getOrInit(AttributeHelper.getConceptDatatypeUuid(attributeUuids)));
        concept.setCreator(1L);
        concept.setDateCreated(DEFAULT_DATE);
        return concept;
    }
}
