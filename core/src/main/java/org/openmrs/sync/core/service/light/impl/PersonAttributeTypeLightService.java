package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class PersonAttributeTypeLightService extends AbstractLightService<PersonAttributeTypeLight> {

    public PersonAttributeTypeLightService(final OpenMrsRepository<PersonAttributeTypeLight> repository) {
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
