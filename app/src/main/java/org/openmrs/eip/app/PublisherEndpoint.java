package org.openmrs.eip.app;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrseip",
        title = "OpenMRS EIP Publisher",
        syntax = "openmrseip:listener",
        label = "openmrs,eip,publisher",
        producerOnly = true
)
public class PublisherEndpoint extends DefaultEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(PublisherEndpoint.class);

    public PublisherEndpoint(String endpointUri) {
        this.setEndpointUri(endpointUri);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new PublisherProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException();
    }

}
