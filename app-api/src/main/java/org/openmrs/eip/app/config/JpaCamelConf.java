package org.openmrs.eip.app.config;

import org.apache.camel.component.jpa.DefaultTransactionStrategy;
import org.apache.camel.component.jpa.JpaComponent;
import org.openmrs.eip.component.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import jakarta.persistence.EntityManagerFactory;

public class JpaCamelConf {
	
	private EntityManagerFactory entityManagerFactory;
	
	public JpaCamelConf(@Qualifier(value = Constants.MGT_ENTITY_MGR) EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	@Bean(value = "jpa")
	public JpaComponent jpa() {
		JpaComponent comp = new JpaComponent();
		comp.setEntityManagerFactory(entityManagerFactory);
		comp.setTransactionStrategy(new DefaultTransactionStrategy(comp.getCamelContext(), entityManagerFactory));
		comp.setSharedEntityManager(true);
		comp.setJoinTransaction(false);
		return comp;
	}
}
