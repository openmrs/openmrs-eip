package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.common.model.sync.LocationModel;
import org.openmrs.sync.component.entity.Location;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
