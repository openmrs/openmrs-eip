package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderAttributeTypeLightService extends AbstractAttributeTypeLightService<ProviderAttributeTypeLight> {

    public ProviderAttributeTypeLightService(final OpenmrsRepository<ProviderAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderAttributeTypeLight createEntity() {
        return new ProviderAttributeTypeLight();
    }
}
