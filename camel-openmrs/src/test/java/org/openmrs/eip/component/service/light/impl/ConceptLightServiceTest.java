package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptClassLight;
import org.openmrs.eip.component.entity.light.ConceptDatatypeLight;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConceptLightServiceTest {
	
	@Mock
	private OpenmrsRepository<ConceptLight> repository;
	
	@Mock
	private ConceptClassLightService conceptClassService;
	
	@Mock
	private ConceptDatatypeLightService conceptDatatypeService;
	
	private ConceptLightService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ConceptLightService(repository, conceptClassService, conceptDatatypeService);
	}
	
	@Test
	public void createPlaceholderEntity() {
		// Given
		when(conceptClassService.getOrInitPlaceholderEntity()).thenReturn(getConceptClass());
		when(conceptDatatypeService.getOrInitPlaceholderEntity()).thenReturn(getConceptDatatype());
		String uuid = "uuid";
		
		// When
		ConceptLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedConcept(), result);
	}
	
	private ConceptLight getExpectedConcept() {
		ConceptClassLight conceptClass = getConceptClass();
		
		ConceptDatatypeLight conceptDatatype = getConceptDatatype();
		
		ConceptLight expected = new ConceptLight();
		expected.setConceptClass(conceptClass);
		expected.setDatatype(conceptDatatype);
		expected.setCreator(1L);
		expected.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		return expected;
	}
	
	private ConceptClassLight getConceptClass() {
		ConceptClassLight conceptClass = new ConceptClassLight();
		conceptClass.setUuid("PLACEHOLDER_CONCEPT_CLASS");
		return conceptClass;
	}
	
	private ConceptDatatypeLight getConceptDatatype() {
		ConceptDatatypeLight conceptDatatype = new ConceptDatatypeLight();
		conceptDatatype.setUuid("PLACEHOLDER_CONCEPT_DATATYPE");
		return conceptDatatype;
	}
}
