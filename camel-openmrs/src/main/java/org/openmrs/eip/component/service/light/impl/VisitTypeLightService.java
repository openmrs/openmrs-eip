package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.VisitTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class VisitTypeLightService extends AbstractLightService<VisitTypeLight> {
	
	public VisitTypeLightService(final OpenmrsRepository<VisitTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected VisitTypeLight createPlaceholderEntity(final String uuid) {
		VisitTypeLight visitType = new VisitTypeLight();
		visitType.setName(DEFAULT_STRING);
		visitType.setCreator(SyncContext.getAppUser().getId());
		visitType.setDateCreated(DEFAULT_DATE);
		return visitType;
	}
}
