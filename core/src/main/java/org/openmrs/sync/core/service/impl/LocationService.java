package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Location;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.LocationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class LocationService extends AbstractEntityService<Location, LocationModel> {

    public LocationService(final SyncEntityRepository<Location> repository,
                           final EntityMapper<Location, LocationModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.LOCATION;
    }
}
