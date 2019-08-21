package org.openmrs.sync.app.config;

import org.openmrs.sync.odoo.config.OdooProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("org.openmrs.sync.odoo")
@ConditionalOnProperty(name = "odoo.activated", havingValue = "true")
@Configuration
public class OdooConf {

    @Bean
    @ConfigurationProperties(prefix = "odoo")
    public OdooProperties odooProperties() {
        return new OdooProperties();
    }
}
