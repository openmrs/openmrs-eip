package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.RelationshipTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class RelationshipTypeLightService extends AbstractLightService<RelationshipTypeLight> {
	
	public RelationshipTypeLightService(final OpenmrsRepository<RelationshipTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected RelationshipTypeLight createPlaceholderEntity(final String uuid) {
		RelationshipTypeLight relationshipType = new RelationshipTypeLight();
		relationshipType.setCreator(SyncContext.getAppUser().getId());
		relationshipType.setDateCreated(DEFAULT_DATE);
		relationshipType.setAIsToB(DEFAULT_STRING);
		relationshipType.setBIsToA(DEFAULT_STRING);
		relationshipType.setPreferred(false);
		relationshipType.setWeight(0);
		
		return relationshipType;
	}
}
