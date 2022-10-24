package org.openmrs.eip.component.camel;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

public class OpenmrsComponentTest {
	
	@Mock
	private CamelContext context;
	
	@Mock
	private ApplicationContext applicationContext;
	
	private OpenmrsComponent component;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		component = new OpenmrsComponent(context, applicationContext);
	}
	
	@Test
	public void createEndPoint_should_return_endpoint() {
		// Given
		
		// When
		Endpoint result = component.createEndpoint("testUri", "extract", new HashMap<>());
		
		// Then
		assertTrue(result instanceof OpenmrsEndpoint);
	}
}
