package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Location;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.LocationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class LocationService extends AbstractEntityService<Location, LocationModel> {

    public LocationService(final SyncEntityRepository<Location> repository,
                           final EntityToModelMapper<Location, LocationModel> entityToModelMapper,
                           final ModelToEntityMapper<LocationModel, Location> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.LOCATION;
    }
}
