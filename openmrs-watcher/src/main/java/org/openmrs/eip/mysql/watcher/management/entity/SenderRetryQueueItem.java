package org.openmrs.eip.mysql.watcher.management.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.openmrs.eip.app.management.entity.BaseRetryQueueItem;
import org.openmrs.eip.mysql.watcher.Event;

@Entity
@Table(name = "sender_retry_queue")
public class SenderRetryQueueItem extends BaseRetryQueueItem {
	
	public static final long serialVersionUID = 1;
	
	@Embedded
	@AttributeOverride(name = "identifier", column = @Column(updatable = false))
	@AttributeOverride(name = "primaryKeyId", column = @Column(name = "primary_key_id", nullable = false, updatable = false))
	@AttributeOverride(name = "tableName", column = @Column(name = "table_name", nullable = false, updatable = false, length = 100))
	@AttributeOverride(name = "operation", column = @Column(nullable = false, updatable = false, length = 1))
	@AttributeOverride(name = "snapshot", column = @Column(nullable = false, updatable = false))
	private Event event;
	
	//the destination where this event couldn't be processed, typically a uri
	@Column(name = "destination", nullable = false, updatable = false, length = 50)
	private String route;
	
	/**
	 * Gets the event
	 *
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}
	
	/**
	 * Gets the route
	 *
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}
	
	/**
	 * Sets the route
	 *
	 * @param route the route to set
	 */
	public void setRoute(String route) {
		this.route = route;
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
		return getClass().getSimpleName() + " {route=" + getRoute() + ", attemptCount=" + getAttemptCount() + ", " + event
		        + "}";
	}
	
}
