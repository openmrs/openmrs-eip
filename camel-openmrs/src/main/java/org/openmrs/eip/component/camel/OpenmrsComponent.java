package org.openmrs.eip.component.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenmrsComponent extends DefaultComponent {
	
	private ApplicationContext applicationContext;
	
	public OpenmrsComponent(final CamelContext context, final ApplicationContext applicationContext) {
		super(context);
		this.applicationContext = applicationContext;
	}
	
	@Override
	protected Endpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) {
		SyncActionEnum action = SyncActionEnum.getAction(remaining);
		return new OpenmrsEndpoint(uri, this, applicationContext, action);
	}
}
