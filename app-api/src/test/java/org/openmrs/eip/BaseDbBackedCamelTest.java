package org.openmrs.eip;

import static org.testcontainers.utility.MountableFile.forClasspathResource;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openmrs.eip.app.SyncConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import({ TestDBConfig.class })
@DirtiesContext
@TestExecutionListeners(value = { DeleteDataTestExecutionListener.class, SqlScriptsTestExecutionListener.class })
@TestPropertySource(properties = "spring.jpa.properties.hibernate.hbm2ddl.auto=update")
@TestPropertySource(properties = "spring.mngt-datasource.driverClassName=org.h2.Driver")
@TestPropertySource(properties = "spring.mngt-datasource.jdbcUrl=jdbc:h2:mem:test;DB_CLOSE_DELAY=30;LOCK_TIMEOUT=10000")
@TestPropertySource(properties = "spring.mngt-datasource.username=sa")
@TestPropertySource(properties = "spring.mngt-datasource.password=test")
@TestPropertySource(properties = "spring.mngt-datasource.dialect=org.hibernate.dialect.H2Dialect")
@TestPropertySource(properties = "spring.openmrs-datasource.dialect=org.hibernate.dialect.MySQL5Dialect")
public abstract class BaseDbBackedCamelTest extends BaseCamelTest {
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer("mysql:5.7.31");
	
	protected static Integer mysqlPort;
	
	protected static final String SCRIPT_DIR = "/test_scripts/";
	
	private static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
	
	@Autowired
	@Qualifier(SyncConstants.MGT_DATASOURCE_NAME)
	protected DataSource mngtDataSource;
	
	@Autowired
	@Qualifier(SyncConstants.OPENMRS_DATASOURCE_NAME)
	protected DataSource openmrsDataSource;
	
	@BeforeClass
	public static void startMysql() throws Exception {
		mysqlContainer.withEnv("MYSQL_ROOT_PASSWORD", "test");
		mysqlContainer.withDatabaseName("openmrs");
		mysqlContainer.withCopyFileToContainer(forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");

		Resource[] scripts = RESOURCE_RESOLVER.getResources("classpath*:" + SCRIPT_DIR + "*.sql");
		
		for (Resource script : scripts) {
			String scriptFile = SCRIPT_DIR + script.getFilename();
			log.info("Adding init SQL file from classpath to MySQL test container -> " + scriptFile);
			mysqlContainer.withCopyFileToContainer(forClasspathResource(scriptFile),
			    "/docker-entrypoint-initdb.d/" + script.getFilename());
		}
		
		Startables.deepStart(Stream.of(mysqlContainer)).join();
		mysqlPort = mysqlContainer.getMappedPort(3306);
	}
	
	@AfterClass
	public static void stopMysql() {
		mysqlContainer.stop();
	}
	
}
