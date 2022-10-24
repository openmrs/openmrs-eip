package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.GaacAffinityTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class GaacAffinityTypeLightService extends AbstractLightService<GaacAffinityTypeLight> {
	
	public GaacAffinityTypeLightService(final OpenmrsRepository<GaacAffinityTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected GaacAffinityTypeLight createPlaceholderEntity(final String uuid) {
		GaacAffinityTypeLight type = new GaacAffinityTypeLight();
		type.setName(DEFAULT_STRING);
		type.setCreator(SyncContext.getAppUser().getId());
		type.setDateCreated(DEFAULT_DATE);
		return type;
	}
}
