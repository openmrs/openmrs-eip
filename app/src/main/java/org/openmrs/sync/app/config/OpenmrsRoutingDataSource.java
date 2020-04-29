package org.openmrs.sync.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class OpenmrsRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(OpenmrsRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        final String key = DataSourceNameHolder.get();
        log.info("Looking up datasource for OpenMRS DB: " + key);
        return key;
    }

}
