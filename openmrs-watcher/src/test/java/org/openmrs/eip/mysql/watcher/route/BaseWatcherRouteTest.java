package org.openmrs.eip.mysql.watcher.route;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_ERROR_HANDLER;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.mysql.watcher.Event;
import org.openmrs.eip.mysql.watcher.WatcherTestUtils;
import org.openmrs.eip.mysql.watcher.config.WatcherConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for tests for routes that wish to be notified of DB events in the backing OpenMRS
 * database, .
 */
@Import(WatcherConfig.class)
@TestPropertySource(properties = PROP_URI_ERROR_HANDLER + "=" + TestConstants.URI_TEST_ERROR_HANDLER)
@TestPropertySource("classpath:watcher-application-test.properties")
public abstract class BaseWatcherRouteTest extends BaseDbBackedCamelTest {
	
	protected static final String PROP_RETRY_MAP = "route-retry-count-map";
	
	protected Event createEvent(String table, String pkId, String identifier, String operation) {
		return WatcherTestUtils.createEvent(table, pkId, identifier, operation);
	}
	
}
