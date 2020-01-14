package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.ProviderLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderLightService extends AbstractLightService<ProviderLight> {

    public ProviderLightService(final OpenmrsRepository<ProviderLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderLight createPlaceholderEntity(final String uuid) {
        ProviderLight provider = new ProviderLight();
        provider.setDateCreated(DEFAULT_DATE);
        provider.setCreator(DEFAULT_USER_ID);
        return provider;
    }
}
