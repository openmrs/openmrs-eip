package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.FormLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class FormLightService extends AbstractLightService<FormLight> {
	
	public FormLightService(final OpenmrsRepository<FormLight> repository) {
		super(repository);
	}
	
	@Override
	protected FormLight createPlaceholderEntity(final String uuid) {
		FormLight form = new FormLight();
		form.setName(DEFAULT_STRING);
		form.setVersion(DEFAULT_STRING);
		form.setCreator(SyncContext.getAppUser().getId());
		form.setDateCreated(DEFAULT_DATE);
		return form;
	}
}
