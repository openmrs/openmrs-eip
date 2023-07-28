package org.openmrs.eip;

import org.openmrs.eip.app.SyncConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.MapPropertySource;

import com.mysql.jdbc.Driver;

/**
 * Test BeanPostProcessor that sets the dead letter uri for tests and injects the OpenMRS datasource
 * properties values after the {@link org.testcontainers.containers.MySQLContainer} has been started
 * and available. This is necessary primarily for setting the MySQL port and the jdbc url.
 */
public class AppPropertiesBeanPostProcessor implements BeanPostProcessor {
	
	@Autowired
	private TestDatabase testDatabase;
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME.equals(beanName)) {
			MapPropertySource propSource = (MapPropertySource) bean;
			propSource.getSource().put("openmrs.db.port", testDatabase.getMysqlPort());
			propSource.getSource().put("openmrs.db.host", "localhost");
			propSource.getSource().put("openmrs.db.name", testDatabase.getDbName());
			propSource.getSource().put("spring.openmrs-datasource.jdbcUrl", testDatabase.getJdbcUrl() + "?useSSL=false");
			propSource.getSource().put("spring.openmrs-datasource.driverClassName", Driver.class.getName());
			propSource.getSource().put("spring.openmrs-datasource.username", "root");
			propSource.getSource().put("spring.openmrs-datasource.password", testDatabase.getPassword());
		}
		
		return bean;
	}
	
}
