package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderAttributeTypeLightService extends AbstractAttributeTypeLightService<ProviderAttributeTypeLight> {

    public ProviderAttributeTypeLightService(final OpenMrsRepository<ProviderAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderAttributeTypeLight createEntity() {
        return new ProviderAttributeTypeLight();
    }
}
