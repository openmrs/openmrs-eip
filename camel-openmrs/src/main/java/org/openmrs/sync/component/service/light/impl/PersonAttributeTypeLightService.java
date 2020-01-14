package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PersonAttributeTypeLightService extends AbstractLightService<PersonAttributeTypeLight> {

    public PersonAttributeTypeLightService(final OpenmrsRepository<PersonAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected PersonAttributeTypeLight createPlaceholderEntity(final String uuid) {
        PersonAttributeTypeLight personAttributeType = new PersonAttributeTypeLight();
        personAttributeType.setDateCreated(DEFAULT_DATE);
        personAttributeType.setCreator(DEFAULT_USER_ID);
        personAttributeType.setName(DEFAULT_STRING);
        return personAttributeType;
    }
}
