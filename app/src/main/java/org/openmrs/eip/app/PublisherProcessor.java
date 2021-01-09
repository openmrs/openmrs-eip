package org.openmrs.eip.app;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("publisher-processor")
public class PublisherProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PublisherProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("\nIn Processor..." + exchange.getMessage().getBody());
    }

}
