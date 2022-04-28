package org.openmrs.eip.mysql.watcher;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(TestOpenmrsDataSourceConfig.class)
@TestPropertySource(properties = "db-event.destinations=mock:db-event-processor")
@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=false")
public abstract class BaseWatcherDbBackedTest extends BaseDbBackedCamelTest {}
