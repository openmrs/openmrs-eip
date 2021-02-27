package org.openmrs.eip.mysql.watcher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.mysql.watcher.config.WatcherConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

/**
 * Base class for tests for routes that wish to be notified of DB events in the backing OpenMRS
 * database.
 */
@Import(WatcherConfig.class)
public abstract class BaseWatcherTest extends BaseDbBackedCamelTest {
	
	@Autowired
	protected ConfigurableEnvironment env;
	
	@BeforeClass
	public static void startContainers() {
		mysqlContainer.withCopyFileToContainer(MountableFile.forClasspathResource("my.cnf"), "/etc/mysql/my.cnf");
		Startables.deepStart(Stream.of(mysqlContainer)).join();
	}
	
	@Before
	public void setup() throws Exception {
		Map<String, Object> props = new HashMap();
		props.put("openmrs.db.port", mysqlContainer.getMappedPort(3306));
		PropertySource testPropSource = new MapPropertySource("test", props);
		env.getPropertySources().addLast(testPropSource);
		loadXmlRoutesInDirectory("watcher-routes", "watcher-error-handler.xml");
		loadXmlRoutesInDirectory("camel-test", "init-test.xml");
	}
	
}
