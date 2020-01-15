package org.openmrs.sync.component.camel;

import org.apache.camel.impl.DefaultProducer;
import org.springframework.context.ApplicationContext;

public abstract class AbstractOpenmrsProducer extends DefaultProducer {

    protected ApplicationContext applicationContext;
    protected ProducerParams params;

    public AbstractOpenmrsProducer(final OpenmrsEndpoint endpoint,
                                   final ApplicationContext applicationContext,
                                   final ProducerParams params) {
        super(endpoint);
        this.applicationContext = applicationContext;
        this.params = params;
    }
}
