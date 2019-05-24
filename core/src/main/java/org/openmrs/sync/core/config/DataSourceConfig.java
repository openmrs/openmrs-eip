package org.openmrs.sync.core.config;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public interface DataSourceConfig {

    DataSource dataSource();

    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory);

    LocalContainerEntityManagerFactoryBean entityManager(EntityManagerFactoryBuilder builder, DataSource dataSource);
}
