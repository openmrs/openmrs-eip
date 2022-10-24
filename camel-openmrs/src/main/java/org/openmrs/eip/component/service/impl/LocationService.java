package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.LocationModel;
import org.openmrs.eip.component.entity.Location;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
