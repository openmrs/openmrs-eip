package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.RelationshipLight;
import org.openmrs.eip.component.entity.light.RelationshipTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class RelationshipLightService extends AbstractLightService<RelationshipLight> {

	  private LightService<PersonLight> personService;
	  private LightService<RelationshipTypeLight> relationshipService;
	  					   
    public RelationshipLightService(final OpenmrsRepository<RelationshipLight> repository,
                                   final LightService<PersonLight> personService) {
        super(repository);
        this.personService = personService;
    }

    @Override
    protected RelationshipLight createPlaceholderEntity(final String uuid) {
    	RelationshipLight relationship = new RelationshipLight();
        relationship.setDateCreated(DEFAULT_DATE);
        relationship.setCreator(DEFAULT_USER_ID);
        relationship.setPersona(personService.getOrInitPlaceholderEntity());
        relationship.setPersonb(personService.getOrInitPlaceholderEntity());
        relationship.setRelationshipType(relationshipService.getOrInitPlaceholderEntity());
        
        return relationship;
    }
}
