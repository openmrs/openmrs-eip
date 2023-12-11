package org.openmrs.eip;

import static org.testcontainers.utility.MountableFile.forClasspathResource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class SharedMysqlContainer extends MySQLContainer<SharedMysqlContainer> {
	
	private static final Logger log = LoggerFactory.getLogger(SharedMysqlContainer.class);
	
	private static final String SCRIPT_DIR = "test_scripts/";
	
	private static final String IMAGE_VERSION = "mysql:5.7.31";
	
	private static SharedMysqlContainer container;
	
	private static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
	
	private static boolean scriptsExecuted = false;
	
	private SharedMysqlContainer() {
		super(IMAGE_VERSION);
		this.waitStrategy = Wait.forLogMessage(".*MySQL init process done.*", 1);
	}
	
	public static SharedMysqlContainer getInstance() {
		if (container == null) {
			container = new SharedMysqlContainer();
		}
		return container;
	}
	
	@Override
	public void start() {
		container.withEnv("MYSQL_ROOT_PASSWORD", "test");
		container.withDatabaseName("openmrs");
		container.withCopyFileToContainer(forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");
		
		super.start();
		
		executeLiquibase();
		executeScripts();
	}
	
	@Override
	public void stop() {
		//do nothing, JVM handles shut down
	}
	
	private void executeLiquibase() {
		// Execute Liquibase changelog file
		try {
			// Load JDBC driver and create a connection to the MySQL container
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(),
			    container.getPassword());
			
			// Run Liquibase changes
			liquibase.integration.commandline.Main
			        .run(new String[] { "--changeLogFile=liquibase-openmrs-test.xml", "--url=" + container.getJdbcUrl(),
			                "--username=" + container.getUsername(), "--password=" + container.getPassword(), "update" });
			
			conn.close();
		}
		catch (Exception e) {
			log.error("Liquibase execution failed: {}", e.getMessage());
			throw new RuntimeException("Liquibase execution failed", e);
		}
	}
	
	private void executeScripts() {
		// Run scripts only if they haven't been executed before
		if (!scriptsExecuted) {
			List<Resource> scripts = new ArrayList<>();
			try {
				scripts = List.of(RESOURCE_RESOLVER.getResources("classpath*:" + SCRIPT_DIR + "*.sql"));
			}
			catch (IOException e) {
				log.error("An IOException occurred while trying to retrieve SQL scripts from the classpath {}",
				    e.getMessage());
				throw new RuntimeException("Failed to retrieve SQL scripts", e);
			}
			
			scripts.forEach(
			    script -> container.withCopyFileToContainer(forClasspathResource(SCRIPT_DIR + script.getFilename()),
			        "/docker-entrypoint-initdb.d/" + script.getFilename()));
			
			scriptsExecuted = true; // Mark scripts as executed
		}
	}
}
