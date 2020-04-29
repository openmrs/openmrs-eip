package org.openmrs.sync.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceNameHolder {

    private static final Logger log = LoggerFactory.getLogger(DataSourceNameHolder.class);

    private static final ThreadLocal<String> KEY_HOLDER = new ThreadLocal();

    public static void set(String dataSourceName) {
        log.info("Setting OpenMRS context DB to: " + dataSourceName);
        KEY_HOLDER.set(dataSourceName);
    }

    public static String get() {
        return KEY_HOLDER.get();
    }

    public static void clear() {
        log.info("Clearing OpenMRS context DB from: " + get());
        KEY_HOLDER.remove();
    }

}
