package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowStateLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProgramWorkflowStateLightServiceTest {
	
	@Mock
	private OpenmrsRepository<ProgramWorkflowStateLight> repository;
	
	@Mock
	private LightService<ConceptLight> conceptService;
	
	@Mock
	private LightService<ProgramWorkflowLight> programWorkflowService;
	
	private ProgramWorkflowStateLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ProgramWorkflowStateLightService(repository, conceptService, programWorkflowService);
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
		when(programWorkflowService.getOrInitPlaceholderEntity()).thenReturn(getProgramWorkflow());
		String uuid = "uuid";
		
		// When
		ProgramWorkflowStateLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedProgramWorkflow(), result);
	}
	
	private ProgramWorkflowStateLight getExpectedProgramWorkflow() {
		ProgramWorkflowStateLight programWorkflowState = new ProgramWorkflowStateLight();
		programWorkflowState.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		programWorkflowState.setCreator(USER_ID);
		programWorkflowState.setConcept(getConcept());
		programWorkflowState.setProgramWorkflow(getProgramWorkflow());
		return programWorkflowState;
	}
	
	private ProgramWorkflowLight getProgramWorkflow() {
		ProgramWorkflowLight programWorkflow = new ProgramWorkflowLight();
		programWorkflow.setUuid("PLACEHOLDER_PROGRAM_WORKFLOW");
		return programWorkflow;
	}
	
	private ConceptLight getConcept() {
		ConceptLight concept = new ConceptLight();
		concept.setUuid("PLACEHOLDER_CONCEPT");
		return concept;
	}
}
