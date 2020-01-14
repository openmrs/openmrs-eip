package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class LocationAttributeTypeLightService extends AbstractAttributeTypeLightService<LocationAttributeTypeLight> {

    public LocationAttributeTypeLightService(final OpenmrsRepository<LocationAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected LocationAttributeTypeLight createEntity() {
        return new LocationAttributeTypeLight();
    }
}
