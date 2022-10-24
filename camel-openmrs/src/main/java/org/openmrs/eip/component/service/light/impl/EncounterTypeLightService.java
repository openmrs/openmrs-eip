package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class EncounterTypeLightService extends AbstractLightService<EncounterTypeLight> {
	
	public EncounterTypeLightService(final OpenmrsRepository<EncounterTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected EncounterTypeLight createPlaceholderEntity(final String uuid) {
		EncounterTypeLight encounterType = new EncounterTypeLight();
		encounterType.setName(DEFAULT_STRING + " - " + uuid);
		encounterType.setCreator(SyncContext.getAppUser().getId());
		encounterType.setDateCreated(DEFAULT_DATE);
		return encounterType;
	}
}
