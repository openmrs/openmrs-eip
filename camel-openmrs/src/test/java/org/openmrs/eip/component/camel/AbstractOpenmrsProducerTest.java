package org.openmrs.eip.component.camel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

public class AbstractOpenmrsProducerTest {
	
	@Mock
	private OpenmrsEndpoint endpoint;
	
	@Mock
	private ApplicationContext applicationContext;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void constructor_should_create_producer() {
		// Given;
		ProducerParams params = ProducerParams.builder().build();
		
		// When
		FakeOpenmrsProducer result = new FakeOpenmrsProducer(endpoint, applicationContext, params);
		
		// Then
		assertNotNull(FakeOpenmrsProducer.appContext);
		assertNotNull(result.getEndpoint());
		assertEquals(params, result.params);
	}
}
