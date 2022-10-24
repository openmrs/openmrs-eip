package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.GaacReasonLeavingTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class GaacReasonLeavingLightService extends AbstractLightService<GaacReasonLeavingTypeLight> {
	
	public GaacReasonLeavingLightService(final OpenmrsRepository<GaacReasonLeavingTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected GaacReasonLeavingTypeLight createPlaceholderEntity(final String uuid) {
		GaacReasonLeavingTypeLight reasonLeaving = new GaacReasonLeavingTypeLight();
		reasonLeaving.setName(DEFAULT_STRING);
		reasonLeaving.setCreator(SyncContext.getAppUser().getId());
		reasonLeaving.setDateCreated(DEFAULT_DATE);
		return reasonLeaving;
	}
}
