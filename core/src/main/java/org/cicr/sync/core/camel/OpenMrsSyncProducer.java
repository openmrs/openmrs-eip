package org.cicr.sync.core.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

public class OpenMrsSyncProducer extends DefaultProducer {

    private EntityNameEnum entityName;

    public OpenMrsSyncProducer(Endpoint endpoint, EntityNameEnum entityName) {
        super(endpoint);
        this.entityName = entityName;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("fqdsfqsfd");
    }
}
