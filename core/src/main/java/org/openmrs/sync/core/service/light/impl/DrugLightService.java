package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.DrugLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.DrugContext;
import org.springframework.stereotype.Service;

@Service
public class DrugLightService extends AbstractLightService<DrugLight, DrugContext> {

    private LightService<ConceptLight, ConceptContext> conceptService;

    public DrugLightService(final OpenMrsRepository<DrugLight> repository,
                            final LightService<ConceptLight, ConceptContext> conceptService) {
        super(repository);
        this.conceptService = conceptService;
    }

    @Override
    protected DrugLight getShadowEntity(final String uuid, final DrugContext context) {
        DrugLight drug = new DrugLight();
        drug.setUuid(uuid);
        drug.setDateCreated(DEFAULT_DATE);
        drug.setCreator(DEFAULT_USER_ID);
        drug.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));
        return drug;
    }

    private ConceptContext getConceptContext(final DrugContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
