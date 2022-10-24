package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.GaacFamilyLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class GaacFamilyLightService extends AbstractLightService<GaacFamilyLight> {
	
	private LightService<LocationLight> locationService;
	
	public GaacFamilyLightService(final OpenmrsRepository<GaacFamilyLight> repository) {
		super(repository);
	}
	
	@Override
	protected GaacFamilyLight createPlaceholderEntity(final String uuid) {
		GaacFamilyLight gaac = new GaacFamilyLight();
		gaac.setFamilyIdentifier(DEFAULT_STRING);
		gaac.setCrumbled(0);
		gaac.setLocation(locationService.getOrInitPlaceholderEntity());
		gaac.setDateCreated(DEFAULT_DATE);
		gaac.setCreator(SyncContext.getAppUser().getId());
		gaac.setStartDate(DEFAULT_DATE);
		return gaac;
	}
}
