package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderLightService extends AbstractLightService<ProviderLight> {

    public ProviderLightService(final OpenMrsRepository<ProviderLight> repository) {
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
