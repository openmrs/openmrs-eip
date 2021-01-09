package org.openmrs.eip.app;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers and calls the debezium route
 */
public class PublisherProducer extends DefaultProducer {

    private static final Logger logger = LoggerFactory.getLogger(PublisherProducer.class);

    public PublisherProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Registering debezium route");

        exchange.getContext().addRoutes(new DebeziumRoute((PublisherEndpoint) getEndpoint()));
    }

}
