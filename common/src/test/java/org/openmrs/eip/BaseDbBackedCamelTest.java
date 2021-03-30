package org.openmrs.eip;

import static org.testcontainers.utility.DockerImageName.parse;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.openmrs.eip.component.common.CommonConstants;
import org.openmrs.eip.component.config.OpenmrsDataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import({ TestDBConfig.class, ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class })
@TestExecutionListeners(value = DeleteDataTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@TestPropertySource(properties = "spring.jpa.properties.hibernate.hbm2ddl.auto=update")
@Transactional
public abstract class BaseDbBackedCamelTest extends BaseCamelTest {
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer<>(parse("mysql:5.6"));
	
	protected static Integer MYSQL_PORT;
	
	@Autowired
	@Qualifier("mngtDataSource")
	protected DataSource mngtDataSource;
	
	@Autowired
	@Qualifier(CommonConstants.OPENMRS_DATASOURCE_NAME)
	protected DataSource openmrsDataSource;
	
	@BeforeClass
	public static void startContainers() {
		mysqlContainer.withEnv("MYSQL_ROOT_PASSWORD", "test");
		mysqlContainer.withDatabaseName("openmrs");
		mysqlContainer.withCopyFileToContainer(MountableFile.forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");
		Startables.deepStart(Stream.of(mysqlContainer)).join();
		MYSQL_PORT = mysqlContainer.getMappedPort(3306);
	}
	
}
