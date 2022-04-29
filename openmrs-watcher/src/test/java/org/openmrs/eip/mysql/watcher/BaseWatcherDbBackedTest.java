package org.openmrs.eip.mysql.watcher;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(TestOpenmrsDataSourceConfig.class)
@TestPropertySource(properties = "db-event.destinations=mock:db-event-processor")
@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=false")
@TestPropertySource(properties = "eip.watchedTables=")
public abstract class BaseWatcherDbBackedTest extends BaseDbBackedCamelTest {}
