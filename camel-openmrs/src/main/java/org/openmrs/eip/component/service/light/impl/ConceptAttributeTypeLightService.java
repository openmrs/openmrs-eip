package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeTypeLightService extends AbstractAttributeTypeLightService<ConceptAttributeTypeLight> {
	
	public ConceptAttributeTypeLightService(final OpenmrsRepository<ConceptAttributeTypeLight> repository) {
		super(repository);
	}
	
	@Override
	protected ConceptAttributeTypeLight createEntity() {
		return new ConceptAttributeTypeLight();
	}
}
