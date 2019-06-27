package org.openmrs.sync.central.config;

import org.openmrs.sync.core.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManager",
        basePackages = {"org.openmrs.sync.core.repository"}
)
public class OpenMrsDataSourceCentralConfig implements DataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Override
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManager")
    @Override
    public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
                                                                @Qualifier("dataSource") final DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("org.openmrs.sync.core.entity")
                .persistenceUnit("openmrs")
                .build();
    }

    @Primary
    @Bean
    @Override
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManager") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
