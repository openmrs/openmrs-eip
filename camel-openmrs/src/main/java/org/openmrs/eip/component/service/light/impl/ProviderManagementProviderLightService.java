package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ProviderManagementProviderLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderManagementProviderLightService extends AbstractLightService<ProviderManagementProviderLight> {

    public ProviderManagementProviderLightService(final OpenmrsRepository<ProviderManagementProviderLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderManagementProviderLight createPlaceholderEntity(final String uuid) {
    	ProviderManagementProviderLight type = new ProviderManagementProviderLight();
        type.setName(DEFAULT_STRING);
        type.setCreator(DEFAULT_USER_ID);
        type.setDateCreated(DEFAULT_DATE);
        return type;
    }
}
