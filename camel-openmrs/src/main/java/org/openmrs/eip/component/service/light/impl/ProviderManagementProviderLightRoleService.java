package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ProviderManagementProviderRoleLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ProviderManagementProviderLightRoleService extends AbstractLightService<ProviderManagementProviderRoleLight> {

    public ProviderManagementProviderLightRoleService(final OpenmrsRepository<ProviderManagementProviderRoleLight> repository) {
        super(repository);
    }

    @Override
    protected ProviderManagementProviderRoleLight createPlaceholderEntity(final String uuid) {
    	ProviderManagementProviderRoleLight type = new ProviderManagementProviderRoleLight();
        type.setName(DEFAULT_STRING);
        type.setCreator(DEFAULT_USER_ID);
        type.setDateCreated(DEFAULT_DATE);
        return type;
    }
}
