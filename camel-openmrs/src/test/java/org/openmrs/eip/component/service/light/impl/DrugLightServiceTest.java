package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.DrugLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class DrugLightServiceTest {
	
	@Mock
	private OpenmrsRepository<DrugLight> repository;
	
	@Mock
	private LightService<ConceptLight> conceptService;
	
	private DrugLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new DrugLightService(repository, conceptService);
		UserLight user = new UserLight();
		user.setId(USER_ID);
		SyncContext.setAppUser(user);
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void createPlaceHolderEntity() {
		// Given
		when(conceptService.getOrInitPlaceholderEntity()).thenReturn(getConcept());
		String uuid = "uuid";
		
		// When
		DrugLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedDrug(), result);
	}
	
	private DrugLight getExpectedDrug() {
		DrugLight drug = new DrugLight();
		drug.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		drug.setCreator(USER_ID);
		drug.setConcept(getConcept());
		return drug;
	}
	
	private ConceptLight getConcept() {
		ConceptLight concept = new ConceptLight();
		concept.setUuid("PLACEHOLDER_CONCEPT");
		return concept;
	}
}
