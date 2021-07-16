package org.openmrs.eip.camel;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;

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
import org.openmrs.eip.Constants;
import org.springframework.util.ReflectionUtils;
import org.testcontainers.shaded.org.apache.commons.lang.reflect.FieldUtils;

public class OauthProcessorTest {
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private ExtendedCamelContext mockCamelContext;
	
	private OauthProcessor processor = new OauthProcessor();
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		setField("producerTemplate", processor, mockProducerTemplate);
	}
	
	private void setField(String name, Object target, Object value) {
		Field field = FieldUtils.getField(target.getClass(), name, true);
		ReflectionUtils.setField(field, target, value);
	}
	
	@Test
	public void process_shouldSkipSettingTheOauthHeaderIfDisabled() throws Exception {
		processor.process(null);
		
		Mockito.verifyNoInteractions(mockProducerTemplate);
	}
	
	@Test
	public void process_shouldCallTheOauthRouteAndSetTheOauthHeaderIfEnabled() throws Exception {
		final String expectedToken = "some-token";
		setField("isOauthEnabled", processor, true);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class))
		        .thenReturn(singletonMap(OauthProcessor.FIELD_TOKEN, expectedToken));
		
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		Assert.assertEquals(OauthProcessor.HTTP_AUTH_SCHEME + " " + expectedToken,
		    exchange.getIn().getHeader(Constants.HTTP_HEADER_AUTH));
	}
	
}
