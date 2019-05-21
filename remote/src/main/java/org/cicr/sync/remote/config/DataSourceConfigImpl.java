package org.cicr.sync.remote.config;

import org.cicr.sync.core.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Component
@EnableTransactionManagement
public class DataSourceConfigImpl extends DataSourceConfig {

    @Value("${spring.datasource.driver}")
    public String dbDriver;

    @Value("${spring.datasource.url}")
    public String dbUrl;

    @Value("${spring.datasource.username}")
    public String dbUserName;

    @Value("${spring.datasource.password}")
    public String dbPassword;

    @Override
    protected String getDbDriver() {
        return dbDriver;
    }

    @Override
    protected String getDbUrl() {
        return dbUrl;
    }

    @Override
    protected String getDbUserName() {
        return dbUserName;
    }

    @Override
    protected String getDbPassword() {
        return dbPassword;
    }
}
