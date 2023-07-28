package org.openmrs.eip;

import static org.testcontainers.utility.MountableFile.forClasspathResource;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

public final class TestDatabase {
	
	private static final Logger log = LoggerFactory.getLogger(TestDatabase.class);
	
	protected static final String SCRIPT_DIR = "/test_scripts/";
	
	private static final PathMatchingResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
	
	private MySQLContainer MYSQL_CONTAINER;
	
	public String getJdbcUrl() {
		return MYSQL_CONTAINER.getJdbcUrl();
	}
	
	public Integer getMysqlPort() {
		return MYSQL_CONTAINER.getMappedPort(3306);
	}
	
	public String getDbName() {
		return MYSQL_CONTAINER.getDatabaseName();
	}
	
	public String getPassword() {
		return MYSQL_CONTAINER.getPassword();
	}
	
	public void start() throws Exception {
		MYSQL_CONTAINER = new MySQLContainer("mysql:5.7.31");
		MYSQL_CONTAINER.withEnv("MYSQL_ROOT_PASSWORD", "test");
		MYSQL_CONTAINER.withDatabaseName("openmrs");
		MYSQL_CONTAINER.withCopyFileToContainer(forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");
		
		Resource[] scripts = RESOLVER.getResources("classpath*:" + SCRIPT_DIR + "*.sql");
		
		for (Resource script : scripts) {
			String scriptFile = SCRIPT_DIR + script.getFilename();
			log.info("Adding init SQL file from classpath to MySQL test container -> " + scriptFile);
			MYSQL_CONTAINER.withCopyFileToContainer(forClasspathResource(scriptFile),
			    "/docker-entrypoint-initdb.d/" + script.getFilename());
		}
		
		Startables.deepStart(Stream.of(MYSQL_CONTAINER)).join();
	}
	
	public void shutdown() {
		MYSQL_CONTAINER.stop();
		MYSQL_CONTAINER = null;
	}
	
}
