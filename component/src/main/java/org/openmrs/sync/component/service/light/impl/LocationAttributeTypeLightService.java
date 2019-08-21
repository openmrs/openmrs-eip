package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
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
