package org.openmrs.sync.app.config;

import org.openmrs.sync.app.SyncProfiles;
import org.openmrs.sync.component.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

@Profile(SyncProfiles.SENDER)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mngtEntityManager",
        transactionManagerRef = "mngtTransactionManager",
        basePackages = {"org.openmrs.sync.app.management.repository"}
)
public class ManagementDataSourceConfig implements DataSourceConfig {

    @Value("${spring.mngt-datasource.dialect}")
    private String hibernateDialect;

    @Value("${spring.mngt-datasource.ddlAuto}")
    private String ddlAuto;

    @Bean(name = "mngtDataSource")
    @ConfigurationProperties(prefix = "spring.mngt-datasource")
    @Override
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mngtEntityManager")
    @Override
    public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
                                                                @Qualifier("mngtDataSource") final DataSource dataSource) {

        Map<String, String> props = new HashMap<>();
        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.hbm2ddl.auto", ddlAuto);

        return builder
                .dataSource(dataSource)
                .packages("org.openmrs.sync.app.management.entity")
                .persistenceUnit("mngt")
                .properties(props)
                .build();
    }


    @Bean(name = "mngtTransactionManager")
    @Override
    public PlatformTransactionManager transactionManager(
            @Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
