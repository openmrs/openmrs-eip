package org.openmrs.sync.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "openmrsEntityManager",
        transactionManagerRef = "openmrsTransactionManager",
        basePackages = {"org.openmrs.sync.component.repository"}
)
public class OpenmrsDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenmrsDataSourceConfig.class);

    @Value("${spring.openmrs-datasource.dialect}")
    private String hibernateDialect;

    @Primary
    @Bean(name = "openmrsDataSource")
    @ConfigurationProperties(prefix = "spring.openmrs-datasource")
    @DependsOn("mngtDataSource")
    public DataSource dataSource(@Qualifier("mngtDataSource") DataSource mngtDataSource, Environment env) throws SQLException {
        Map<Object, Object> dbNameDataSourceMap = new HashMap();
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = mngtDataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM openmrs_db");
            final String prefix = "spring.";
            while (rs.next()) {
                String dbName = rs.getString(1);
                log.info("Building datasource for OpenMRS DB: " + dbName);
                DataSource ds = DataSourceBuilder.create().driverClassName(env.getRequiredProperty(prefix + dbName +
                        ".driverClassName")).url(env.getRequiredProperty(prefix + dbName + ".jdbcUrl"))
                        .username(env.getRequiredProperty(prefix + dbName + ".username")).
                                password(env.getRequiredProperty(prefix + dbName + ".password")).build();

                dbNameDataSourceMap.put(dbName, ds);
            }
            rs.close();

        } finally {
            if (conn != null) {
                conn.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        OpenmrsRoutingDataSource dataSource = new OpenmrsRoutingDataSource();
        dataSource.setLenientFallback(false);
        dataSource.setTargetDataSources(dbNameDataSourceMap);
        //Spring needs to pull DB metadata info at startup when there is no context DS set, so lets just set a default
        //TODO Unset the default target DS in one of the bean's life cycle methods, we should never have a default DB
        dataSource.setDefaultTargetDataSource(dbNameDataSourceMap.values().iterator().next());

        return dataSource;
    }

    @Primary
    @Bean(name = "openmrsEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
                                                                @Qualifier("openmrsDataSource") final DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("org.openmrs.sync.component.entity")
                .persistenceUnit("openmrs")
                .properties(
                        singletonMap(
                                "hibernate.dialect",
                                "org.hibernate.dialect.MySQL5InnoDBDialect"
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
