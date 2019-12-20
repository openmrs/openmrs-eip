package org.openmrs.sync.app.config;

import org.apache.camel.component.jpa.JpaComponent;
import org.openmrs.sync.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.persistence.EntityManagerFactory;

@Profile(SyncProfiles.SENDER)
@Configuration
public class JpaCamelConf {

    private EntityManagerFactory entityManagerFactory;

    public JpaCamelConf(@Qualifier(value = "mngtEntityManager") final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean(value = "jpa")
    public JpaComponent jpa() {
        JpaComponent comp = new JpaComponent();
        comp.setEntityManagerFactory(entityManagerFactory);

        return comp;
    }
}
