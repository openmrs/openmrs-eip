package org.openmrs.eip;

import static org.testcontainers.utility.MountableFile.forClasspathResource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

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
		executeLiquibase("liquibase-openmrs-test.xml");
		executeScripts();
	}
	
	public void executeLiquibase(String changeLogFile) {
		try (Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(),
		    container.getPassword())) {
			
			Database database = DatabaseFactory.getInstance()
			        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
			
			Liquibase liquibase = new liquibase.Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
			
			CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
			updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
			updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, liquibase.getChangeLogFile());
			updateCommand.execute();
		}
		catch (Exception exception) {
			log.error("Liquibase execution failed: {}", exception.getMessage());
			throw new RuntimeException("Liquibase execution failed", exception);
		}
	}
	
	private void executeScripts() {
		if (!scriptsExecuted) {
			List<Resource> scripts;
			try {
				scripts = List.of(RESOURCE_RESOLVER.getResources("classpath*:" + SCRIPT_DIR + "*.sql"));
				
				scripts.forEach(
				    script -> container.withCopyFileToContainer(forClasspathResource(SCRIPT_DIR + script.getFilename()),
				        "/docker-entrypoint-initdb.d/" + script.getFilename()));
			}
			catch (IOException e) {
				log.error("An IOException occurred while trying to retrieve SQL scripts from the classpath {}",
				    e.getMessage());
				throw new RuntimeException("Failed to retrieve SQL scripts", e);
			}
			finally {
				scriptsExecuted = true;
			}
		}
	}
}
