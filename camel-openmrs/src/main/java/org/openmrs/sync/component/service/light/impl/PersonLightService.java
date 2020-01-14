package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.PersonLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PersonLightService extends AbstractLightService<PersonLight> {

    public PersonLightService(final OpenmrsRepository<PersonLight> repository) {
        super(repository);
    }

    @Override
    protected PersonLight createPlaceholderEntity(final String uuid) {
        PersonLight person = new PersonLight();
        person.setDateCreated(DEFAULT_DATE);
        return person;
    }
}
