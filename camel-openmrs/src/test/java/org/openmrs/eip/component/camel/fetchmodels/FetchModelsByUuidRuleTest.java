package org.openmrs.eip.component.camel.fetchmodels;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.camel.ProducerParams;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class FetchModelsByUuidRuleTest {
	
	@Mock
	private EntityServiceFacade facade;
	
	private FetchModelsByUuidRule rule;
	
	private static final String UUID = "UUID";
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		rule = new FetchModelsByUuidRule(facade);
	}
	
	@Test
	public void evaluate_should_return_true() {
		// Given
		ProducerParams params = ProducerParams.builder().uuid(UUID).build();
		
		// When
		boolean result = rule.evaluate(params);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void evaluate_should_return_false() {
		// Given
		ProducerParams params = ProducerParams.builder().id(1L).build();
		
		// When
		boolean result = rule.evaluate(params);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void getModels_should_call_facade() {
		// Given
		ProducerParams params = ProducerParams.builder().uuid(UUID).tableToSync(TableToSyncEnum.PERSON).build();
		
		// When
		rule.getModels(params);
		
		// Then
		verify(facade).getModel(TableToSyncEnum.PERSON, UUID);
	}
	
}
