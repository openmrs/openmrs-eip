package org.openmrs.eip.app.config;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.component.jpa.JpaComponent;
import org.openmrs.eip.component.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

public class JpaCamelConf {
	
	private EntityManagerFactory entityManagerFactory;
	
	private PlatformTransactionManager transactionManager;
	
	public JpaCamelConf(@Qualifier(value = Constants.MGT_ENTITY_MGR) EntityManagerFactory entityManagerFactory,
	    @Qualifier(value = Constants.MGT_TX_MGR) PlatformTransactionManager transactionManager) {
		this.entityManagerFactory = entityManagerFactory;
		this.transactionManager = transactionManager;
	}
	
	@Bean(value = "jpa")
	public JpaComponent jpa() {
		JpaComponent comp = new JpaComponent();
		comp.setEntityManagerFactory(entityManagerFactory);
		comp.setTransactionManager(transactionManager);
		return comp;
	}
}
