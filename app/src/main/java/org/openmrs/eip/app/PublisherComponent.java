package org.openmrs.eip.app;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("publisher-component")
public class PublisherComponent extends DefaultComponent {

    private static final Logger logger = LoggerFactory.getLogger(PublisherComponent.class);

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new PublisherEndpoint(uri);
    }

}
