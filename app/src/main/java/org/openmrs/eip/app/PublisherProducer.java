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

    private String listener;

    public PublisherProducer(Endpoint endpoint, String listener) {
        super(endpoint);
        this.listener = listener;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Registering debezium route with listener: " + listener);
        exchange.getContext().addRoutes(new DebeziumRoute(listener));
    }

}
