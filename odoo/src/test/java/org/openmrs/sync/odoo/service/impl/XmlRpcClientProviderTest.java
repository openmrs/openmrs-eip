package org.openmrs.sync.odoo.service.impl;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class XmlRpcClientProviderTest {

    private XmlRpcClientProvider provider = new XmlRpcClientProvider();

    @Test
    public void getClient_should_return_client() {
        // Given

        // When
        XmlRpcClient result = provider.getClient();

        // Then
        assertNotNull(result);
    }
}
