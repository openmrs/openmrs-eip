package org.openmrs.sync.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public abstract class DataSourceConfig {

    protected abstract String getDbDriver();
    protected abstract String getDbUrl();
    protected abstract String getDbUserName();
    protected abstract String getDbPassword();

    @Bean("dataSource")
    public DataSource getConfig() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(getDbDriver());
        dataSource.setUrl(getDbUrl());
        dataSource.setUsername(getDbUserName());
        dataSource.setPassword(getDbPassword());

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new JpaTransactionManager();
    }
}
