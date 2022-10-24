package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ProgramLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProgramWorkflowLightServiceTest {
	
	@Mock
	private OpenmrsRepository<ProgramWorkflowLight> repository;
	
	@Mock
	private LightService<ConceptLight> conceptService;
	
	@Mock
	private LightService<ProgramLight> programService;
	
	private ProgramWorkflowLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ProgramWorkflowLightService(repository, conceptService, programService);
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
		when(conceptService.getOrInitPlaceholderEntity()).thenReturn(getConcept());
		when(programService.getOrInitPlaceholderEntity()).thenReturn(getProgram());
		String uuid = "uuid";
		
		// When
		ProgramWorkflowLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedProgramWorkflow(), result);
	}
	
	private ProgramWorkflowLight getExpectedProgramWorkflow() {
		ProgramWorkflowLight programWorkflow = new ProgramWorkflowLight();
		programWorkflow.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		programWorkflow.setCreator(USER_ID);
		programWorkflow.setConcept(getConcept());
		programWorkflow.setProgram(getProgram());
		return programWorkflow;
	}
	
	private ProgramLight getProgram() {
		ProgramLight program = new ProgramLight();
		program.setUuid("PLACEHOLDER_PROGRAM");
		return program;
	}
	
	private ConceptLight getConcept() {
		ConceptLight concept = new ConceptLight();
		concept.setUuid("PLACEHOLDER_CONCEPT");
		return concept;
	}
}
