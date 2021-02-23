package org.openmrs.eip.mysql.watcher.route;

import org.apache.camel.processor.idempotent.jpa.JpaMessageIdRepository;
import org.openmrs.eip.BaseDbBackedCamelContextTest;
import org.openmrs.eip.mysql.watcher.config.WatcherConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * Base class for tests for routes that process
 */
@Import(WatcherConfig.class)
public abstract class BaseWatcherRouteTest extends BaseDbBackedCamelContextTest {
	
}
