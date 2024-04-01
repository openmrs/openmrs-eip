package org.openmrs.eip.fhir.components;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

@Component("openmrs-fhir")
public class FhirSearchBundleComponent extends DefaultComponent {
	
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		Endpoint endpoint = new FhirSearchBundleEndpoint(uri, this);
		setProperties(endpoint, parameters);
		return endpoint;
	}
}
