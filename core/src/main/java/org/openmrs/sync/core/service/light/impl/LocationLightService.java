package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class LocationLightService extends AbstractLightService<LocationLight> {

    public LocationLightService(final OpenMrsRepository<LocationLight> repository) {
        super(repository);
    }

    @Override
    protected LocationLight getFakeEntity(final String uuid) {
        LocationLight location = new LocationLight();
        location.setUuid(uuid);
        location.setName(DEFAULT_STRING);
        location.setCreator(DEFAULT_USER_ID);
        location.setDateCreated(DEFAULT_DATE);
        return location;
    }
}
