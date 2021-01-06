package org.openmrs.eip.app;

import org.apache.camel.CamelContext;
import org.apache.camel.component.debezium.DebeziumComponent;
import org.apache.camel.component.debezium.DebeziumEndpoint;
import org.apache.camel.component.debezium.DebeziumMySqlComponent;
import org.apache.camel.component.debezium.DebeziumMySqlEndpoint;
import org.apache.camel.component.debezium.configuration.MySqlConnectorEmbeddedDebeziumConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.utils.odoo.workordermanager.rule.NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DebeziumMySqlComponentWrapper extends DebeziumComponent<MySqlConnectorEmbeddedDebeziumConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(DebeziumMySqlComponentWrapper.class);

    //private DebeziumMySqlComponent delegate;
    

    private MySqlConnectorEmbeddedDebeziumConfiguration configuration;

    public DebeziumMySqlComponentWrapper() {
    }

    public DebeziumMySqlComponentWrapper(CamelContext context) {
        super(context);
    }

    @Override
    public MySqlConnectorEmbeddedDebeziumConfiguration getConfiguration() {
        return this.configuration == null ? new MySqlConnectorEmbeddedDebeziumConfiguration() : this.configuration;
    }

    @Override
    public void setConfiguration(MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected DebeziumEndpoint initializeDebeziumEndpoint(String uri, MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        logger.info("\nOther uri: "+uri);
        uri = StringUtils.replace(uri, "openmrseip", "debezium-mysql");
        return new DebeziumMySqlEndpointWrapper(uri, this, configuration);
    }

    @Override
    protected DebeziumEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        logger.info("\n\nCreating endpoint...");
        logger.info("uri: "+uri);
        logger.info("Remaining: "+remaining);
        logger.info("Params: "+parameters);
        uri = StringUtils.replace(uri, "openmrseip", "debezium-mysql");
        logger.info("NEW uri: "+uri);
        return super.createEndpoint(uri, remaining, parameters);
    }
}
