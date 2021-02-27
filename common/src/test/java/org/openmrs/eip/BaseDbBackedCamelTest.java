package org.openmrs.eip;

import static org.testcontainers.utility.DockerImageName.parse;

import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import(ManagementDataSourceConfig.class)
public abstract class BaseDbBackedCamelTest extends BaseCamelTest {
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer<>(parse("mysql:5.6")).withEnv("MYSQL_ROOT_PASSWORD",
	    "test");
	
	protected static final Integer MYSQL_PORT = mysqlContainer.getMappedPort(3306);
	
}
