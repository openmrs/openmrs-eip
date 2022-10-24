package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ProgramLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class ProgramLightService extends AbstractLightService<ProgramLight> {
	
	private LightService<ConceptLight> conceptService;
	
	public ProgramLightService(final OpenmrsRepository<ProgramLight> repository,
	    final LightService<ConceptLight> conceptService) {
		super(repository);
		this.conceptService = conceptService;
	}
	
	@Override
	protected ProgramLight createPlaceholderEntity(final String uuid) {
		ProgramLight program = new ProgramLight();
		program.setDateCreated(DEFAULT_DATE);
		program.setCreator(SyncContext.getAppUser().getId());
		program.setName(DEFAULT_STRING);
		program.setConcept(conceptService.getOrInitPlaceholderEntity());
		
		return program;
	}
}
