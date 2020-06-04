package org.openmrs.sync.app;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.sync.app.management.init.impl.ManagementDbInitImpl;
import org.openmrs.sync.component.SyncProfiles;
import org.openmrs.sync.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication(scanBasePackages = {
        "org.openmrs.sync.app",
        "org.openmrs.sync.component",
        "org.openmrs.utils.odoo"
})
public class SyncApplication {

    private ManagementDbInitImpl managementDbInit;

    private CamelContext camelContext;

    public SyncApplication(@Nullable final ManagementDbInitImpl managementDbInit,
                           final CamelContext camelContext) {
        this.managementDbInit = managementDbInit;
        this.camelContext = camelContext;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SyncApplication.class, args);
    }

    @PostConstruct
    private void initDb() {
        if (managementDbInit != null) {
            managementDbInit.start();
        }
    }

    @PostConstruct
    private void addTypeConverter() {
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
    }

    @PostConstruct
    private void addBCProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Bean to handle messages in error and re-route them to another route
     *
     * @return deadLetterChannelBuilder
     */
    @Bean
    public DeadLetterChannelBuilder deadLetterChannelBuilder() {
        DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dlc");
        builder.setUseOriginalMessage(true);
        return builder;
    }

    @Bean
    @Profile(SyncProfiles.SENDER)
    public PropertySource getCustomPropertySource(ConfigurableEnvironment env) {
        //Custom PropertySource that we can dynamically populate with generated property values which
        //is not possible via the properties file e.g. to specify names of tables to sync.
        final String dbName = env.getProperty("debezium.db.name");
        Set<String> tables = new HashSet(TableToSyncEnum.values().length);
        for (TableToSyncEnum tableToSyncEnum : TableToSyncEnum.values()) {
            tables.add(dbName + "." + tableToSyncEnum.name());
        }

        Map<String, Object> props = Collections.singletonMap("debezium.tablesToSync", StringUtils.join(tables, ","));
        PropertySource customPropSource = new MapPropertySource("custom", props);
        env.getPropertySources().addLast(customPropSource);

        return customPropSource;
    }

}
