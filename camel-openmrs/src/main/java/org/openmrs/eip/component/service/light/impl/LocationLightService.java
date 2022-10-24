package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class LocationLightService extends AbstractLightService<LocationLight> {
	
	public LocationLightService(final OpenmrsRepository<LocationLight> repository) {
		super(repository);
	}
	
	@Override
	protected LocationLight createPlaceholderEntity(final String uuid) {
		LocationLight location = new LocationLight();
		location.setName(DEFAULT_STRING);
		location.setCreator(SyncContext.getAppUser().getId());
		location.setDateCreated(DEFAULT_DATE);
		return location;
	}
}
