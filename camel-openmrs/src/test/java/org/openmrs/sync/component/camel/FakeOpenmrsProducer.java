package org.openmrs.sync.component.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.springframework.context.ApplicationContext;

public class FakeOpenmrsProducer extends AbstractOpenmrsProducer {

    public FakeOpenmrsProducer(final Endpoint endpoint,
                               final ApplicationContext applicationContext,
                               final ProducerParams params) {
        super(endpoint, applicationContext, params);
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        // Nothing
    }
}
