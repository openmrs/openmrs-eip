package org.openmrs.eip;

import javax.sql.DataSource;

import org.openmrs.eip.config.DatasourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@ContextConfiguration(classes = { DatasourceConfig.class })
@TestExecutionListeners(value = { DeleteDataTestExecutionListener.class, SqlScriptsTestExecutionListener.class })
@TestPropertySource(properties = "spring.jpa.properties.hibernate.hbm2ddl.auto=update")
@TestPropertySource(properties = "spring.mngt-datasource.driverClassName=org.h2.Driver")
@TestPropertySource(properties = "spring.mngt-datasource.jdbcUrl=jdbc:h2:mem:test;DB_CLOSE_DELAY=30;LOCK_TIMEOUT=10000;MODE=LEGACY")
@TestPropertySource(properties = "logging.level.org.hibernate.tool.schema.internal.ExceptionHandlerLoggedImpl=ERROR")
@TestPropertySource(properties = "spring.mngt-datasource.username=sa")
@TestPropertySource(properties = "spring.mngt-datasource.password=test")
@TestPropertySource(properties = "spring.mngt-datasource.dialect=org.hibernate.dialect.H2Dialect")
@TestPropertySource(properties = "spring.openmrs-datasource.driverClassName=com.mysql.cj.jdbc.Driver")
@TestPropertySource(properties = "spring.openmrs-datasource.dialect=org.hibernate.dialect.MySQLDialect")
public abstract class BaseDbBackedCamelTest extends BaseCamelTest {
	
	protected static SharedMysqlContainer container = SharedMysqlContainer.getInstance();
	
	static {
		container.start();
	}
	
	@Autowired
	@Qualifier(Constants.MGT_DATASOURCE_NAME)
	protected DataSource mngtDataSource;
	
	@Autowired
	@Qualifier(Constants.OPENMRS_DATASOURCE_NAME)
	protected DataSource openmrsDataSource;
	
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.openmrs-datasource.jdbcUrl", () -> container.getJdbcUrl() + "?useSSL=false");
		registry.add("spring.openmrs-datasource.username", container::getUsername);
		registry.add("spring.openmrs-datasource.password", container::getPassword);
		registry.add("spring.openmrs-datasource.driverClassName", container::getDriverClassName);
		registry.add("openmrs.db.port", () -> container.getMappedPort(3306));
		registry.add("openmrs.db.host", container::getHost);
		registry.add("openmrs.db.name", container::getDatabaseName);
	}
}
