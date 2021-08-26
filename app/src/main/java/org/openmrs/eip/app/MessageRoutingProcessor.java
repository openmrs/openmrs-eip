package org.openmrs.eip.app;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("msgRoutingProcessor")
public class MessageRoutingProcessor implements Processor {
	
	protected static final Logger log = LoggerFactory.getLogger(MessageRoutingProcessor.class);
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Routing DB sync message received from the message broker");
		}
		
		producerTemplate.send("direct:message-processor", exchange);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully routed DB sync message");
		}
		
	}
}
