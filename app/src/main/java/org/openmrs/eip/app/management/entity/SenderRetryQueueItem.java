package org.openmrs.eip.app.management.entity;

import org.openmrs.eip.component.entity.Event;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Column(name="destination", nullable = false, updatable = false, length = 50)
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
     * Sets the event
     *
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {route=" + getRoute() + ", attemptCount=" + getAttemptCount() + ", " + event + "}";
    }

}
