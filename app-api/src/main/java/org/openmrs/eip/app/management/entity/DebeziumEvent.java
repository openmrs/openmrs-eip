package org.openmrs.eip.app.management.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.openmrs.eip.component.entity.Event;

@Entity
@Table(name = "debezium_event_queue")
public class DebeziumEvent extends AbstractEntity {
	
	private static final long serialVersionUID = -1884382844867650350L;
	
	@Embedded
	@AttributeOverride(name = "identifier", column = @Column(updatable = false))
	@AttributeOverride(name = "primaryKeyId", column = @Column(name = "primary_key_id", nullable = false, updatable = false))
	@AttributeOverride(name = "tableName", column = @Column(name = "table_name", nullable = false, updatable = false, length = 100))
	@AttributeOverride(name = "operation", column = @Column(nullable = false, updatable = false, length = 1))
	@AttributeOverride(name = "snapshot", column = @Column(nullable = false, updatable = false))
	@AttributeOverride(name = "requestUuid", column = @Column(name = "request_uuid", unique = true, updatable = false, length = 38))
	private Event event;
	
	/**
	 * Gets the event
	 *
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}
	
	/**
	 * Sets the event
	 *
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {id=" + getId() + ", event=" + event + "}";
	}
	
}
