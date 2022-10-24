package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class CareSettingLightService extends AbstractLightService<CareSettingLight> {
	
	public CareSettingLightService(final OpenmrsRepository<CareSettingLight> repository) {
		super(repository);
	}
	
	@Override
	protected CareSettingLight createPlaceholderEntity(final String uuid) {
		CareSettingLight careSetting = new CareSettingLight();
		careSetting.setDateCreated(DEFAULT_DATE);
		careSetting.setCreator(SyncContext.getAppUser().getId());
		careSetting.setCareSettingType(DEFAULT_STRING);
		careSetting.setName(DEFAULT_STRING);
		return careSetting;
	}
}
