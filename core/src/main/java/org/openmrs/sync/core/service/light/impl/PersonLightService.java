package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonLightService extends AbstractLightService<PersonLight> {

    public PersonLightService(final OpenMrsRepository<PersonLight> repository) {
        super(repository);
    }

    @Override
    protected PersonLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        PersonLight person = new PersonLight();
        person.setUuid(uuid);
        person.setDateCreated(DEFAULT_DATE);
        return person;
    }
}
