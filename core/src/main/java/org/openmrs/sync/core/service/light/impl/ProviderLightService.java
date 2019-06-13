package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class ProviderLightService extends AbstractLightServiceNoContext<ProviderLight> {

    public ProviderLightService(final OpenMrsRepository<ProviderLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderLight getShadowEntity(final String uuid) {
        ProviderLight provider = new ProviderLight();
        provider.setUuid(uuid);
        provider.setDateCreated(DEFAULT_DATE);
        provider.setCreator(DEFAULT_USER_ID);
        return provider;
    }
}
