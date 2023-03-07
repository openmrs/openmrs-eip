package org.openmrs.eip.component.camel.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.reflect.Whitebox;

public class CamelUtilsTest {
	
	private static final String URI = "test:uri";
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	@Before
	public void setupClass() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(CamelUtils.class, ProducerTemplate.class, mockTemplate);
	}
	
	@Test
	public void send_shouldSendTheMessageToTheEndpointWithTheProvidedExchange() {
		Exchange e = new DefaultExchange((CamelContext) null);
		when(mockTemplate.send(URI, e)).thenReturn(e);
		
		Assert.assertEquals(e, CamelUtils.send(URI, e));
		
		verify(mockTemplate).send(URI, e);
	}
	
	@Test
	public void send_shouldFailIfAnEnErrorIsEncounteredByTheTemplate() {
		Exchange exchange = Mockito.mock(Exchange.class);
		when(mockTemplate.send(URI, exchange)).thenReturn(exchange);
		when(exchange.getException()).thenReturn(new Exception());
		Exception thrown = assertThrows(EIPException.class, () -> {
			CamelUtils.send(URI, exchange);
		});
		
		assertEquals("An error occurred while calling endpoint: " + URI, thrown.getMessage());
	}
	
	@Test
	public void send_shouldCreateAnExchangeAndSendTheMessageToTheEndpoint() {
		Exchange e = Mockito.mock(Exchange.class);
		CamelContext c = Mockito.mock(ExtendedCamelContext.class);
		when(mockTemplate.getCamelContext()).thenReturn(c);
		when(mockTemplate.send(eq(URI), any(Exchange.class))).thenReturn(e);
		
		CamelUtils.send(URI);
		
		verify(mockTemplate).send(eq(URI), any(Exchange.class));
	}
	
}
