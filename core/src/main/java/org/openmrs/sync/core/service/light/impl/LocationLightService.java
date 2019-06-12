package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationLightService extends AbstractLightService<LocationLight> {

    public LocationLightService(final OpenMrsRepository<LocationLight> repository) {
        super(repository);
    }

    @Override
    protected LocationLight getFakeEntity(final String uuid,
                                          final List<AttributeUuid> attributeUuids) {
        LocationLight location = new LocationLight();
        location.setUuid(uuid);
        location.setName(DEFAULT_STRING);
        location.setCreator(DEFAULT_USER_ID);
        location.setDateCreated(DEFAULT_DATE);
        return location;
    }
}
