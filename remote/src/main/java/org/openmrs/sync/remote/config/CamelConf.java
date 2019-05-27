package org.openmrs.sync.remote.config;

import org.apache.camel.component.jpa.JpaComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class CamelConf {

    private EntityManagerFactory entityManagerFactory;

    public CamelConf(@Qualifier(value = "mngtEntityManager") final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean(value = "jpa")
    public JpaComponent jpa() {
        JpaComponent comp = new JpaComponent();
        comp.setEntityManagerFactory(entityManagerFactory);

        return comp;
    }
}
