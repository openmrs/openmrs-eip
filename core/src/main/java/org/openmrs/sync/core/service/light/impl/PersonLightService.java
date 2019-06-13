package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class PersonLightService extends AbstractLightServiceNoContext<PersonLight> {

    public PersonLightService(final OpenMrsRepository<PersonLight> repository) {
        super(repository);
    }

    @Override
    protected PersonLight getShadowEntity(final String uuid) {
        PersonLight person = new PersonLight();
        person.setUuid(uuid);
        person.setDateCreated(DEFAULT_DATE);
        return person;
    }
}
