package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.EncounterRoleLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class EncounterRoleLightService extends AbstractLightService<EncounterRoleLight> {
	
	public EncounterRoleLightService(final OpenmrsRepository<EncounterRoleLight> repository) {
		super(repository);
	}
	
	@Override
	protected EncounterRoleLight createPlaceholderEntity(final String uuid) {
		EncounterRoleLight role = new EncounterRoleLight();
		role.setName(DEFAULT_STRING);
		role.setCreator(SyncContext.getAppUser().getId());
		role.setDateCreated(DEFAULT_DATE);
		return role;
	}
}
