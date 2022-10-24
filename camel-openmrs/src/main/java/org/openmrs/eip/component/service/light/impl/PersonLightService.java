package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PersonLightService extends AbstractLightService<PersonLight> {
	
	public PersonLightService(final OpenmrsRepository<PersonLight> repository) {
		super(repository);
	}
	
	@Override
	protected PersonLight createPlaceholderEntity(final String uuid) {
		PersonLight person = new PersonLight();
		person.setCreator(SyncContext.getAppUser().getId());
		person.setDateCreated(DEFAULT_DATE);
		return person;
	}
}
