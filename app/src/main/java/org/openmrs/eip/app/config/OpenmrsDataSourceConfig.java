package org.openmrs.eip.app.config;

import com.zaxxer.hikari.HikariDataSource;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;

import static java.util.Collections.singletonMap;
import static org.openmrs.eip.app.OpenmrsEipConstants.OPENMRS_DATASOURCE_NAME;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "openmrsEntityManager",
        transactionManagerRef = "openmrsTransactionManager",
        basePackages = {"org.openmrs.eip.component.repository"}
)
public class OpenmrsDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenmrsDataSourceConfig.class);

    private static final String CONN_INIT_SQL = "SET @@sql_log_bin=OFF";

    @Value("${spring.openmrs-datasource.dialect}")
    private String hibernateDialect;

    @Primary
    @Bean(name = OPENMRS_DATASOURCE_NAME)
    @ConfigurationProperties(prefix = "spring.openmrs-datasource")
    public DataSource dataSource(Environment env) {
        DataSource ds = DataSourceBuilder.create().build();
        if (Arrays.asList(env.getActiveProfiles()).contains(SyncProfiles.RECEIVER)) {
            if (ds instanceof HikariDataSource) {
                log.info("Setting connection init SQL to: " + CONN_INIT_SQL);
                ((HikariDataSource) ds).setConnectionInitSql(CONN_INIT_SQL);
            } else {
                //TODO support other DS types
                throw new EIPException("Do not know how to initialize datasource of type: " + ds.getClass());
            }
        }

        return ds;
    }

    @Primary
    @Bean(name = "openmrsEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
                                                                @Qualifier(OPENMRS_DATASOURCE_NAME) final DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("org.openmrs.eip.component.entity")
                .persistenceUnit("openmrs")
                .properties(
                        singletonMap(
                                "hibernate.dialect",
                                hibernateDialect
                        )
                )
                .build();
    }

    @Primary
    @Bean(name = "openmrsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("openmrsEntityManager") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
