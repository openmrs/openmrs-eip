package org.openmrs.sync.odoo.service.impl;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.springframework.stereotype.Component;

@Component
public class XmlRpcClientProvider {

    private XmlRpcClient client;

    public XmlRpcClient getClient() {
        if (this.client == null) {
            this.client = new XmlRpcClient();
        }
        return client;
    }
}
