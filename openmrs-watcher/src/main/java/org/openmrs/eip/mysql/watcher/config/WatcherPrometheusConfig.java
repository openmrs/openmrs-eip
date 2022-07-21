package org.openmrs.eip.mysql.watcher.config;

import static org.openmrs.eip.Constants.OPENMRS_DATASOURCE_NAME;

import javax.sql.DataSource;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.mysql.watcher.management.entity.DebeziumEvent;
import org.openmrs.eip.mysql.watcher.management.entity.SenderRetryQueueItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration
public class WatcherPrometheusConfig {
	
	private static final String METER_PREFIX = "openmrs_dbsync_watcher_";
	
	private static final String DS_PREFIX = METER_PREFIX + "datasource_status_";
	
	@Bean("watcherOpenmrsDsHealthIndicator")
	public DataSourceHealthIndicator getDataSourceHealthIndicator(@Autowired @Qualifier(OPENMRS_DATASOURCE_NAME) DataSource dataSource) {
		return new DataSourceHealthIndicator(dataSource);
	}
	
	@Bean("watcherDbEventsMeter")
	public MeterBinder getSyncMessagesMeterBinder(@Autowired ProducerTemplate producerTemplate) {
		
		return (registry) -> Gauge.builder(METER_PREFIX + "db_events", () -> {
			String entity = DebeziumEvent.class.getName();
			return producerTemplate.requestBody("jpa:" + entity + "?query=SELECT count(*) FROM " + entity, null,
			    Integer.class);
		}).register(registry);
		
	}
	
	@Bean("watcherErrorsMeter")
	public MeterBinder getErrorsMeterBinder(@Autowired ProducerTemplate producerTemplate) {
		
		return (registry) -> Gauge.builder(METER_PREFIX + "errors", () -> {
			String entity = SenderRetryQueueItem.class.getName();
			return producerTemplate.requestBody("jpa:" + entity + "?query=SELECT count(*) FROM " + entity, null,
			    Integer.class);
		}).register(registry);
		
	}
	
	@Bean("watcherOpenmrsDsMeter")
	public MeterBinder getOpenmrsDbHealth(@Autowired DataSourceHealthIndicator indicator) {
		
		return (registry) -> {
			Gauge.builder(DS_PREFIX + "openmrs", () -> indicator.health().getStatus().equals(Status.UP) ? 1 : 0)
			        .register(registry);
		};
		
	}
	
}
