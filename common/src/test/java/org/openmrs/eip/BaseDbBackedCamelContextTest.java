package org.openmrs.eip;

import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.springframework.context.annotation.Import;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import(ManagementDataSourceConfig.class)
public abstract class BaseDbBackedCamelContextTest extends BaseCamelContextTest {
	
}
