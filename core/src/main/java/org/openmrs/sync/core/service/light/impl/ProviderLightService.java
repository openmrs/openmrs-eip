package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderLightService extends AbstractLightService<ProviderLight> {

    public ProviderLightService(final OpenMrsRepository<ProviderLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        ProviderLight provider = new ProviderLight();
        provider.setUuid(uuid);
        provider.setDateCreated(DEFAULT_DATE);
        provider.setCreator(DEFAULT_USER_ID);
        return provider;
    }
}
