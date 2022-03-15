package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.OrderGroupLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class OrderGroupLightService extends AbstractLightService<OrderGroupLight> {
	
	private LightService<PatientLight> patientService;
	
	private LightService<EncounterLight> encounterService;
	
	public OrderGroupLightService(OpenmrsRepository<OrderGroupLight> repository, LightService<PatientLight> patientService,
	    LightService<EncounterLight> encounterService) {
		
		super(repository);
		this.patientService = patientService;
		this.encounterService = encounterService;
	}
	
	@Override
	protected OrderGroupLight createPlaceholderEntity(final String uuid) {
		OrderGroupLight orderGroup = new OrderGroupLight();
		orderGroup.setDateCreated(DEFAULT_DATE);
		orderGroup.setCreator(SyncContext.getUser().getId());
		orderGroup.setPatient(patientService.getOrInitPlaceholderEntity());
		orderGroup.setEncounter(encounterService.getOrInitPlaceholderEntity());
		
		return orderGroup;
	}
}
