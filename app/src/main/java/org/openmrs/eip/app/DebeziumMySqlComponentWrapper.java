package org.openmrs.eip.app;

import org.apache.camel.CamelContext;
import org.apache.camel.component.debezium.DebeziumComponent;
import org.apache.camel.component.debezium.DebeziumEndpoint;
import org.apache.camel.component.debezium.DebeziumMySqlComponent;
import org.apache.camel.component.debezium.DebeziumMySqlComponentConfigurer;
import org.apache.camel.component.debezium.DebeziumMySqlEndpoint;
import org.apache.camel.component.debezium.DebeziumMySqlEndpointConfigurer;
import org.apache.camel.component.debezium.configuration.MySqlConnectorEmbeddedDebeziumConfiguration;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.openmrs.utils.odoo.workordermanager.rule.NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Component("openmrseip")
public class DebeziumMySqlComponentWrapper extends DebeziumComponent<MySqlConnectorEmbeddedDebeziumConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(DebeziumMySqlComponentWrapper.class);

    private DebeziumMySqlComponent delegate;
    @Autowired
    private CamelContext camelContext;

    private MySqlConnectorEmbeddedDebeziumConfiguration configuration;

    @PostConstruct
    public void setContext(){
        delegate.setCamelContext(camelContext);
    }

    public DebeziumMySqlComponentWrapper() {
        delegate = new DebeziumMySqlComponent();
        Field field = FieldUtils.getField(DebeziumMySqlComponent.class, "componentPropertyConfigurer", true);
        ReflectionUtils.setField(field, delegate, new DebeziumMySqlComponentConfigurer());
        field = FieldUtils.getField(DebeziumMySqlComponent.class, "endpointPropertyConfigurer", true);
        ReflectionUtils.setField(field, delegate, new DebeziumMySqlEndpointConfigurer());
    }

    public DebeziumMySqlComponentWrapper(CamelContext context) {
        super(context);
        delegate = new DebeziumMySqlComponent(context);
    }

    @Override
    public MySqlConnectorEmbeddedDebeziumConfiguration getConfiguration() {
        //return this.configuration == null ? new MySqlConnectorEmbeddedDebeziumConfiguration() : this.configuration;
        return delegate.getConfiguration();
    }

    @Override
    public void setConfiguration(MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        delegate.setConfiguration(configuration);
    }
    
    @Override
    protected DebeziumEndpoint initializeDebeziumEndpoint(String uri, MySqlConnectorEmbeddedDebeziumConfiguration configuration) {
        //uri = StringUtils.replace(uri, "openmrseip", "debezium-mysql");
        //return new DebeziumMySqlEndpointWrapper(uri, this, configuration);
        

        Method method = MethodUtils.getMatchingMethod(DebeziumMySqlComponent.class, "initializeDebeziumEndpoint", String.class, MySqlConnectorEmbeddedDebeziumConfiguration.class);
        method.setAccessible(true);
        DebeziumEndpoint endpoint = (DebeziumEndpoint)ReflectionUtils.invokeMethod(method, delegate, uri, configuration);
        return endpoint;
    }

    /**
     * Gets the delegate
     *
     * @return the delegate
     */
    public DebeziumMySqlComponent getDelegate() {
        return delegate;
    }
    /*
    @Override
    protected DebeziumEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        uri = StringUtils.replace(uri, "openmrseip", "debezium-mysql");
        //return super.createEndpoint(uri, remaining, parameters);
        return delegate.createEndpoint(uri, remaining, parameters);
    }*/
}
