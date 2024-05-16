package org.openmrs.eip.fhir.components;

import static org.openmrs.eip.fhir.Constants.CSV_PATTERN;

import java.io.InputStream;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultProducer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;

public class FhirSearchBundleProducer extends DefaultProducer {
	
	private static final String DECODED_RESOURCE_KEY = "openmrs-fhir.resource";
	
	private final FhirContext fhirContext = FhirContext.forR4();
	
	public FhirSearchBundleProducer(FhirSearchBundleEndpoint endpoint) {
		super(endpoint);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String resourceType = getResourceType(exchange);
		String resourceId = getResourceId(exchange);
		String includes = ((FhirSearchBundleEndpoint) getEndpoint()).getInclude();
		String revIncludes = ((FhirSearchBundleEndpoint) getEndpoint()).getRevinclude();
		
		exchange.removeProperty(DECODED_RESOURCE_KEY);
		
		StringBuilder urlBuilder = new StringBuilder(resourceType + "?_id=" + resourceId);
		if (includes != null && !includes.isBlank()) {
			for (String include : CSV_PATTERN.split(includes)) {
				urlBuilder.append("&_include=").append(include);
			}
		}
		
		if (revIncludes != null && !revIncludes.isBlank()) {
			for (String revInclude : CSV_PATTERN.split(revIncludes)) {
				urlBuilder.append("&_revinclude=").append(revInclude);
			}
		}
		
		Endpoint fhirSearchEndpoint = getEndpoint().getCamelContext().getEndpoint("fhir://search/searchByUrl?inBody=url");
		exchange.getIn().setBody(urlBuilder.toString());
		fhirSearchEndpoint.createProducer().process(exchange);
	}
	
	private String getResourceType(Exchange exchange) {
		String resourceParam = ((FhirSearchBundleEndpoint) getEndpoint()).getResource();
		
		if (resourceParam != null && !resourceParam.isBlank()) {
			return resourceParam;
		}
		
		IResource resource = incomingMessageToResource(exchange);
		
		if (resource == null) {
			throw new IllegalStateException(
			        "Cannot find the type of the resource to search for. The id must either be supplied as a URL parameter or the message body should be a FHIR resource with a resource type.");
		}
		
		return resource.fhirType();
	}
	
	private String getResourceId(Exchange exchange) {
		String idParam = ((FhirSearchBundleEndpoint) getEndpoint()).getId();
		
		if (idParam != null && !idParam.isBlank()) {
			return idParam;
		}
		
		IResource resource = incomingMessageToResource(exchange);
		
		if (resource == null) {
			throw new IllegalStateException(
			        "Cannot find the id of the resource to search for. The id must either be supplied as a URL parameter or the message body should be a FHIR resource with an id.");
		}
		
		return resource.getId().getValueAsString();
	}
	
	private IResource incomingMessageToResource(Exchange exchange) {
		Object existingProperty = exchange.getProperty(DECODED_RESOURCE_KEY);
		if (existingProperty instanceof IResource) {
			return (IResource) existingProperty;
		}
		
		IResource resource = null;
		Message message = exchange.getMessage();
		if (message instanceof IResource) {
			resource = (IResource) message;
		} else if (message instanceof InputStream) {
			resource = fhirContext.newJsonParser().parseResource(IResource.class, (InputStream) message);
		}
		
		if (message != null) {
			exchange.setProperty(DECODED_RESOURCE_KEY, resource);
		}
		
		return resource;
	}
}
