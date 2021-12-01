package org.openmrs.eip.config;

import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.springframework.context.annotation.Import;

@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class })
public class DatasourceConfig {}
