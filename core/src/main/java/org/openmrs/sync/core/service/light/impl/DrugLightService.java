package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.DrugLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugLightService extends AbstractLightService<DrugLight> {

    private ConceptLightService conceptService;

    public DrugLightService(final OpenMrsRepository<DrugLight> repository,
                            final ConceptLightService conceptService) {
        super(repository);
        this.conceptService = conceptService;
    }

    @Override
    protected DrugLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        DrugLight drug = new DrugLight();
        drug.setUuid(uuid);
        drug.setDateCreated(DEFAULT_DATE);
        drug.setCreator(DEFAULT_USER_ID);
        drug.setConcept(conceptService.getOrInit(AttributeHelper.getConceptUuid(uuids)));
        return drug;
    }
}
