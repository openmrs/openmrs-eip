package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PersonLightService extends AbstractLightService<PersonLight> {

    public PersonLightService(final OpenMrsRepository<PersonLight> repository) {
        super(repository);
    }

    @Override
    protected PersonLight createPlaceholderEntity(final String uuid) {
        PersonLight person = new PersonLight();
        person.setDateCreated(DEFAULT_DATE);
        return person;
    }
}
