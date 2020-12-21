package org.openmrs.eip.publisher.management.entity;

import org.openmrs.eip.component.entity.Event;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "retry_queue")
public class RetryQueueItem extends BaseRetryQueueItem {

    public static final long serialVersionUID = 1;

    @Embedded
    @AttributeOverride(name = "identifier", column = @Column(updatable = false))
    @AttributeOverride(name = "primaryKeyId", column = @Column(name = "primary_key_id", nullable = false, updatable = false))
    @AttributeOverride(name = "tableName", column = @Column(name = "table_name", nullable = false, updatable = false, length = 100))
    @AttributeOverride(name = "operation", column = @Column(nullable = false, updatable = false, length = 1))
    @AttributeOverride(name = "snapshot", column = @Column(nullable = false, updatable = false))
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
        return "RetryQueueItem {route=" + getRoute() + ", attemptCount=" + getAttemptCount() + ", " + event + "}";
    }

}
