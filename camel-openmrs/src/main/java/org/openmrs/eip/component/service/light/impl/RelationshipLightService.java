package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.RelationshipLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class RelationshipLightService extends AbstractLightService<RelationshipLight> {

    private LightService<PersonLight> personService;

    public RelationshipLightService(final OpenmrsRepository<RelationshipLight> repository,
                                   final LightService<PersonLight> personService) {
        super(repository);
        this.personService = personService;
    }

    @Override
    protected RelationshipLight createPlaceholderEntity(final String uuid) {
    	RelationshipLight gaac = new RelationshipLight();
        gaac.setDateCreated(DEFAULT_DATE);
        gaac.setCreator(DEFAULT_USER_ID);
        gaac.setPersona(personService.getOrInitPlaceholderEntity());
        gaac.setPersonb(personService.getOrInitPlaceholderEntity());
        
        return gaac;
    }
}
