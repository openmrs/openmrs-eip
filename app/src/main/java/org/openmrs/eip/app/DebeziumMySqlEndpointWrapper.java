package org.openmrs.eip.app;

import org.apache.camel.component.debezium.DebeziumEndpoint;
import org.apache.camel.component.debezium.DebeziumMySqlComponent;
import org.apache.camel.component.debezium.DebeziumMySqlEndpoint;
import org.apache.camel.component.debezium.configuration.MySqlConnectorEmbeddedDebeziumConfiguration;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrseip",
        title = "OpenMRS EIP Publisher",
        syntax = "openmrseip:listen",
        label = "openmrseip",
        consumerOnly = true
)
public class DebeziumMySqlEndpointWrapper extends DebeziumEndpoint<MySqlConnectorEmbeddedDebeziumConfiguration> {

    //private DebeziumMySqlEndpoint delegate;

    @UriParam
    private MySqlConnectorEmbeddedDebeziumConfiguration configuration;

    public DebeziumMySqlEndpointWrapper() {
    }

    public DebeziumMySqlEndpointWrapper(String uri, DebeziumMySqlComponentWrapper component, MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    @Override
    public MySqlConnectorEmbeddedDebeziumConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        this.configuration = configuration;
    }

}
