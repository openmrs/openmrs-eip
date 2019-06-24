package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class LocationAttributeTypeLightService extends AbstractAttributeTypeLightService<LocationAttributeTypeLight> {

    public LocationAttributeTypeLightService(final OpenMrsRepository<LocationAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected LocationAttributeTypeLight createEntity() {
        return new LocationAttributeTypeLight();
    }
}
