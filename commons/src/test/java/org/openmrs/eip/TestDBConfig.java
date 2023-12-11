package org.openmrs.eip;

import org.openmrs.eip.config.DatasourceConfig;
import org.springframework.context.annotation.Import;

@Import({ DatasourceConfig.class })
public class TestDBConfig {}
