package org.openmrs.eip.mysql.watcher.route;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.WatcherTestConfig;
import org.openmrs.eip.mysql.watcher.WatcherTestConstants;
import org.openmrs.eip.mysql.watcher.config.WatcherConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * Base class for tests for routes that wish to be notified of DB events in the backing OpenMRS
 * database, .
 */
@Import({ WatcherConfig.class, WatcherTestConfig.class })
public abstract class BaseWatcherRouteTest extends BaseDbBackedCamelTest {
	
	protected static final String PROP_EVENT = "event";
	
	protected static final String PROP_RETRY_MAP = "route-retry-count-map";
	
	@Autowired
	protected ConfigurableEnvironment env;
	
	private boolean routesLoaded = false;
	
	@Before
	public void setupBaseWatcherRouteTest() throws Exception {
		Map<String, Object> props = new HashMap();
		props.put("db-event.destinations", WatcherTestConstants.URI_MOCK_EVENT_PROCESSOR);
		PropertySource customPropSource = new MapPropertySource("test", props);
		env.getPropertySources().addLast(customPropSource);
		if (!routesLoaded) {
			loadXmlRoutesInDirectory("watcher-routes", "watcher-error-handler.xml", "db-event-listener.xml");
			routesLoaded = true;
		}
		if (startDebezium()) {
			loadXmlRoutesInDirectory("camel-test", "init-test.xml");
		}
	}
	
	protected Event createEvent(String table, String pkId, String identifier, String operation) {
		Event event = new Event();
		event.setTableName(table);
		event.setPrimaryKeyId(pkId);
		event.setIdentifier(identifier);
		event.setOperation(operation);
		event.setSnapshot(false);
		return event;
	}
	
	/**
	 * Subclasses that need the actual debezium events can override this method and return true
	 * 
	 * @return true if debezium engine should be started otherwise false
	 */
	protected boolean startDebezium() {
		return false;
	}
	
}
