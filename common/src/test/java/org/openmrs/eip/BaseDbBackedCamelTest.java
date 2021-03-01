package org.openmrs.eip;

import static org.testcontainers.utility.DockerImageName.parse;

import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.openmrs.eip.component.config.OpenmrsDataSourceConfig;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import({ TestDBConfig.class, ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class })
public abstract class BaseDbBackedCamelTest extends BaseCamelTest {
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer<>(parse("mysql:5.6"));
	
	protected static Integer MYSQL_PORT;
	
	@BeforeClass
	public static void startContainers() {
		mysqlContainer.withEnv("MYSQL_ROOT_PASSWORD", "test");
		mysqlContainer.withDatabaseName("openmrs");
		mysqlContainer.withCopyFileToContainer(MountableFile.forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");
		Startables.deepStart(Stream.of(mysqlContainer)).join();
		MYSQL_PORT = mysqlContainer.getMappedPort(3306);
	}
	
}
