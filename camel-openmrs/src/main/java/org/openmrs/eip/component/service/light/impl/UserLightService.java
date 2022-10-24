package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class UserLightService extends AbstractLightService<UserLight> {
	
	public UserLightService(final OpenmrsRepository<UserLight> userRepository) {
		super(userRepository);
	}
	
	@Override
	protected UserLight createPlaceholderEntity(final String uuid) {
		UserLight user = new UserLight();
		user.setCreator(SyncContext.getAppUser().getId());
		user.setDateCreated(DEFAULT_DATE);
		user.setSystemId("admin");
		user.setPersonId(1L);
		return user;
	}
}
