package org.openmrs.eip.camel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.EIPException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CamelUtilsTest {
	
	private static final String URI = "test:uri";
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	private AutoCloseable openMocksAutoCloseable;
	
	@BeforeEach
	public void setupClass() {
		this.openMocksAutoCloseable = openMocks(this);
		setInternalState(CamelUtils.class, ProducerTemplate.class, mockTemplate);
	}
	
	@AfterAll
	public void tearDown() throws Exception {
		this.openMocksAutoCloseable.close();
	}
	
	@Test
	public void send_shouldSendTheMessageToTheEndpointWithTheProvidedExchange() {
		Exchange e = new DefaultExchange((CamelContext) null);
		when(mockTemplate.send(URI, e)).thenReturn(e);
		
		assertEquals(e, CamelUtils.send(URI, e));
		
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
		CamelContext c = Mockito.mock(CamelContext.class);
		when(mockTemplate.getCamelContext()).thenReturn(c);
		when(mockTemplate.send(eq(URI), any(Exchange.class))).thenReturn(e);
		
		CamelUtils.send(URI);
		
		verify(mockTemplate).send(eq(URI), any(Exchange.class));
	}
	
}
