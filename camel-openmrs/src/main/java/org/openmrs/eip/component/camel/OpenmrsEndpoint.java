package org.openmrs.eip.component.camel;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.exception.EIPException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@UriEndpoint(firstVersion = "1.0.0", scheme = "openmrs", title = "OpenMRS", syntax = "openmrs:action", producerOnly = true, label = "core,java")
public class OpenmrsEndpoint extends DefaultEndpoint {
	
	@UriPath(name = "action")
	@Metadata(required = true)
	private SyncActionEnum action;
	
	@UriParam(label = "tableToSync")
	private TableToSyncEnum tableToSync;
	
	@UriParam(label = "consumer,advanced")
	private LocalDateTime lastSyncDate;
	
	@UriParam(label = "consumer, advanced")
	private String uuid;
	
	@UriParam(label = "consumer, advanced")
	private Long entityId;
	
	private ApplicationContext applicationContext;
	
	public OpenmrsEndpoint(final String endpointUri, final Component component, final ApplicationContext applicationContext,
	    final SyncActionEnum action) {
		super(endpointUri, component);
		this.action = action;
		this.applicationContext = applicationContext;
	}
	
	@Override
	public Producer createProducer() {
		ProducerParams params = ProducerParams.builder().tableToSync(tableToSync).lastSyncDate(lastSyncDate).id(entityId)
		        .uuid(uuid).build();
		try {
			return action.getProducerClass()
			        .getDeclaredConstructor(OpenmrsEndpoint.class, ApplicationContext.class, ProducerParams.class)
			        .newInstance(this, applicationContext, params);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new EIPException("Unable to initialize producer " + action.getProducerClass().getName(), e);
		}
	}
	
	@Override
	public Consumer createConsumer(final Processor processor) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public TableToSyncEnum getTableToSync() {
		return tableToSync;
	}
	
	public void setTableToSync(final TableToSyncEnum tableToSync) {
		this.tableToSync = tableToSync;
	}
	
	public LocalDateTime getLastSyncDate() {
		return lastSyncDate;
	}
	
	public void setLastSyncDate(final LocalDateTime lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}
	
	public void setEntityId(final Long entityId) {
		this.entityId = entityId;
	}
	
	public Long getEntityId() {
		return entityId;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object instanceof OpenmrsEndpoint) {
			return super.equals(object);
		}
		return false;
	}
}
