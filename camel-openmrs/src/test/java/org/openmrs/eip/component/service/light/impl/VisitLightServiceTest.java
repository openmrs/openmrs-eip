package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.entity.light.VisitLight;
import org.openmrs.eip.component.entity.light.VisitTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class VisitLightServiceTest {
	
	@Mock
	private OpenmrsRepository<VisitLight> repository;
	
	@Mock
	private LightService<PatientLight> patientService;
	
	@Mock
	private LightService<VisitTypeLight> visitTypeService;
	
	private VisitLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new VisitLightService(repository, patientService, visitTypeService);
		UserLight user = new UserLight();
		user.setId(USER_ID);
		SyncContext.setAppUser(user);
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void createPlaceholderEntity() {
		// Given
		when(patientService.getOrInitPlaceholderEntity()).thenReturn(getPatient());
		when(visitTypeService.getOrInitPlaceholderEntity()).thenReturn(getVisitType());
		String uuid = "uuid";
		
		// When
		VisitLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedVisit(), result);
	}
	
	private VisitLight getExpectedVisit() {
		VisitLight visit = new VisitLight();
		visit.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		visit.setCreator(USER_ID);
		visit.setDateStarted(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		visit.setPatient(getPatient());
		visit.setVisitType(getVisitType());
		return visit;
	}
	
	private VisitTypeLight getVisitType() {
		VisitTypeLight visitType = new VisitTypeLight();
		visitType.setUuid("PLACEHOLDER_VISIT_TYPE");
		return visitType;
	}
	
	private PatientLight getPatient() {
		PatientLight patient = new PatientLight();
		patient.setUuid("PLACEHOLDER_PATIENT");
		return patient;
	}
}
