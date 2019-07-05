package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.DrugLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class DrugLightService extends AbstractLightService<DrugLight> {

    private LightService<ConceptLight> conceptService;

    public DrugLightService(final OpenMrsRepository<DrugLight> repository,
                            final LightService<ConceptLight> conceptService) {
        super(repository);
        this.conceptService = conceptService;
    }

    @Override
    protected DrugLight createPlaceholderEntity(final String uuid) {
        DrugLight drug = new DrugLight();
        drug.setDateCreated(DEFAULT_DATE);
        drug.setCreator(DEFAULT_USER_ID);
        drug.setConcept(conceptService.getOrInitPlaceholderEntity());
        return drug;
    }
}
